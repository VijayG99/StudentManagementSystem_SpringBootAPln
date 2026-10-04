package org.example.studentmanagementsystem.dto.Student;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
@Data
public class StudentRequestDto {
    @NotBlank(message = "First name is required")
    @Size(min = 2,max = 50,message="first must be 2 to 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(message ="max = 50,message=\"first must be 2 to 50 characters" )
    private String lastName;

    @NotBlank(message = "Email is required")
    @Size
    @Email(message = "Email format is invalid")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\d{10}$", message = "Phone must be a valid 10 Digit number")
    private String phone;

    @NotNull(message = "Date of Birth is required")
    @Past(message = "Date of Birth must be in the past")
    private LocalDate dateOfBirth;

    private Long departmentId;
}
