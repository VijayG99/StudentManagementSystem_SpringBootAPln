package org.example.studentmanagementsystem.service;



import org.example.studentmanagementsystem.dto.enrollment.EnrollmentRequestDto;
import org.example.studentmanagementsystem.dto.enrollment.EnrollmentResponseDto;

import java.util.List;

public interface EnrollmentRequestService {

    EnrollmentResponseDto apply(EnrollmentRequestDto request);

    EnrollmentResponseDto getById(Long id);

    List<EnrollmentResponseDto> getAll();

    EnrollmentResponseDto approve(Long id);

    EnrollmentResponseDto reject(Long id);
}
