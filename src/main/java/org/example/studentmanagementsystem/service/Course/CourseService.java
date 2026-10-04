package org.example.studentmanagementsystem.service.Course;

import org.example.studentmanagementsystem.dto.Course.CourseResponseDto;
import org.example.studentmanagementsystem.dto.CourseRequestDto;
import org.example.studentmanagementsystem.dto.CourseRequestPatchDto;

import java.util.List;

public interface CourseService {

    // CREATE
    CourseResponseDto createCourse(
            CourseRequestDto request
    );

    // READ
    List<CourseResponseDto> getAllCourses();

    CourseResponseDto getCourseById(
            Long id
    );

    // UPDATE
    CourseResponseDto updateCourse(
            Long id,
            CourseRequestDto request
    );

    CourseResponseDto updateCoursePartially(
            Long id,
            CourseRequestPatchDto request
    );

    // DELETE
    void deleteCourse(Long id);

    // RELATIONSHIP OPERATIONS

    List<Long> getStudentIds(
            Long courseId
    );
}