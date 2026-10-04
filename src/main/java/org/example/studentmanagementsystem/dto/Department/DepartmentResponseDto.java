package org.example.studentmanagementsystem.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentResponseDto {

    private Long id;

    private String name;

    private String code;

    private String description;

    private List<Long> studentIds;

    private List<Long> courseIds;
}