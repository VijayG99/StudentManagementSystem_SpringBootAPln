package org.example.studentmanagementsystem.dto.Course;

import lombok.*;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseResponseDto {

    private Long id;

    private String name;

    private String code;

    private String description;

    private Integer credits;

    private Long departmentId;

    private String departmentName;

    private Set<Long> studentIds;
}