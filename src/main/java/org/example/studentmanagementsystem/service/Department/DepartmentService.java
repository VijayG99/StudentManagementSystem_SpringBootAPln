package org.example.studentmanagementsystem.service.Department;

import org.example.studentmanagementsystem.dto.DepartmentRequestDto;
import org.example.studentmanagementsystem.dto.DepartmentRequestPatchDto;
import org.example.studentmanagementsystem.dto.DepartmentResponseDto;

import java.util.List;

public interface DepartmentService {

    // CREATE
    DepartmentResponseDto createDepartment(
            DepartmentRequestDto request
    );

    // READ
    List<DepartmentResponseDto> getAllDepartments();

    DepartmentResponseDto getDepartmentById(
            Long id
    );

    // UPDATE
    DepartmentResponseDto updateDepartment(
            Long id,
            DepartmentRequestDto request
    );

    DepartmentResponseDto updateDepartmentPartially(
            Long id,
            DepartmentRequestPatchDto request
    );

    // DELETE
    void deleteDepartment(Long id);

    // RELATIONSHIP OPERATIONS
    List<Long> getStudentIds(
            Long departmentId
    );

    List<Long> getCourseIds(
            Long departmentId
    );

    void removeCourseFromDepartment(
            Long departmentId,
            Long courseId
    );
}