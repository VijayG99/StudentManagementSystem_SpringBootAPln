package org.example.studentmanagementsystem.dto.Student;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
public class StudentRequestDto {

    @NotBlank(message = "First name is required")
    @Size(
            min = 2,
            max = 50,
            message = "First name must be 2 to 50 characters"
    )
    private String firstName;


    @NotBlank(message = "Last name is required")
    @Size(
            min = 2,
            max = 50,
            message = "Last name must be 2 to 50 characters"
    )
    private String lastName;


    @NotBlank(message = "Email is required")
    @Email(message = "Email format is invalid")
    private String email;


    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^\\d{10}$",
            message = "Phone must be a valid 10 Digit number"
    )
    private String phone;


    @NotNull(message = "Date of Birth is required")
    @Past(message = "Date of Birth must be in the past")
    private LocalDate dateOfBirth;


    private MultipartFile photo;


    private Long departmentId;
}