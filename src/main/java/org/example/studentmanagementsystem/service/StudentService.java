package org.example.studentmanagementsystem.service;

import org.example.studentmanagementsystem.dto.PageResponse;
import org.example.studentmanagementsystem.dto.Student.StudentRequestDto;
import org.example.studentmanagementsystem.dto.Student.StudentRequestPatchDto;
import org.example.studentmanagementsystem.dto.Student.StudentResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface StudentService {

    // CREATE
    StudentResponseDto createStudent(StudentRequestDto request);

    // READ
    PageResponse<StudentResponseDto> getAllStudents(String search, Pageable pageable);

    StudentResponseDto getStudentById(Long id);

    // UPDATE
    StudentResponseDto updateStudent(
            Long id,
            StudentRequestDto request
    );

    StudentResponseDto updateStudentPartially(
            Long id,
            StudentRequestPatchDto request
    );

    // DELETE
    void deleteStudent(Long id);

    //Multipart file
    void uploadPhoto(Long studentId, MultipartFile file);

    //Retriving Multipart file
    byte[] getStudentPhoto(Long studentId);

    // RELATIONSHIP OPERATIONS

    void assignDepartment(
            Long studentId,
            Long departmentId
    );

    void removeDepartment(
            Long studentId
    );

    void enrollCourse(
            Long studentId,
            Long courseId
    );

    void removeCourse(
            Long studentId,
            Long courseId
    );

    List<Long> getStudentCourseIds(
            Long studentId
    );
}