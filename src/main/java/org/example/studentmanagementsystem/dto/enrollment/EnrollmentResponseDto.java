package org.example.studentmanagementsystem.dto.enrollment;

import lombok.Builder;
import lombok.Data;
import org.example.studentmanagementsystem.entity.EnrollmentStatus;

import java.time.LocalDateTime;

@Data
@Builder
public class EnrollmentResponseDto {

    private Long id;
    private Long studentId;
    private String studentName;
    private Long courseId;
    private String courseTitle;
    private EnrollmentStatus status;
    private LocalDateTime requestedAt;
}
