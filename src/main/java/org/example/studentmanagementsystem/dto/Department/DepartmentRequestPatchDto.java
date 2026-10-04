package org.example.studentmanagementsystem.dto;

import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentRequestPatchDto {

    @Size(max = 100, message = "Department name must not exceed 100 characters")
    private String name;

    @Size(max = 20, message = "Department code must not exceed 20 characters")
    private String code;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
}