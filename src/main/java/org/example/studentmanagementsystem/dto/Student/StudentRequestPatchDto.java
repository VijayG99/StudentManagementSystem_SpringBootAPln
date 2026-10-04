package org.example.studentmanagementsystem.dto.Student;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
@Data
public class StudentRequestPatchDto {

    @Size(min = 2,max = 50,message="first must be 2 to 50 characters")
    private String firstName;


    @Size(message ="max = 50,message=\"first must be 2 to 50 characters" )
    private String lastName;


    @Size
    @Email(message = "Email format is invalid")
    private String email;


    @Pattern(regexp = "^\\d{10}$", message = "Phone must be a valid 10 Digit number")
    private String phone;


    @Past(message = "Date of Birth must be in the past")
    private LocalDate dateOfBirth;

    private Long departmentId;
}
