package org.example.studentmanagementsystem.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseRequestPatchDto {

    @Size(max = 100, message = "Course name must not exceed 100 characters")
    private String name;

    @Size(max = 20, message = "Course code must not exceed 20 characters")
    private String code;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @Min(value = 1, message = "Credits must be at least 1")
    private Integer credits;

    private Long departmentId;
}