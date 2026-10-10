
package org.example.studentmanagementsystem.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.studentmanagementsystem.entity.User;
import org.example.studentmanagementsystem.repository.UserRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader =
                request.getHeader("Authorization");

        log.info("Authorization header received: {}",
                authHeader != null && authHeader.startsWith("Bearer "));

        // No token: continue the chain.
        // Spring Security will decide whether the endpoint is public.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            if (jwtService.isTokenValid(token)
                    && SecurityContextHolder.getContext()
                    .getAuthentication() == null) {

                String email = jwtService.extractEmail(token);

                User user = userRepository.findByEmail(email)
                        .orElse(null);

                if (user != null) {

                    UserDetails userDetails =
                            org.springframework.security.core.userdetails.User
                                    .withUsername(user.getEmail())
                                    .password(user.getPassword())
                                    .authorities(
                                            "ROLE_" + user.getRole().name()
                                    )
                                    .build();

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authentication.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );

                    SecurityContextHolder.getContext()
                            .setAuthentication(authentication);

                    log.info("JWT authentication successful for: {}", email);
                    log.info("Granted authorities: {}", authentication.getAuthorities());
                }
            }
        } catch (io.jsonwebtoken.JwtException
                 | IllegalArgumentException exception) {

            // Invalid or expired token: do not authenticate the request.
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}