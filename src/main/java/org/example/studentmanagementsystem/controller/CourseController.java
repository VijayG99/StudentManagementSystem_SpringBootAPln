package org.example.studentmanagementsystem.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.studentmanagementsystem.dto.Course.CourseResponseDto;
import org.example.studentmanagementsystem.dto.CourseRequestDto;
import org.example.studentmanagementsystem.dto.CourseRequestPatchDto;
import org.example.studentmanagementsystem.service.Course.CourseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@Slf4j
public class CourseController {

    private final CourseService courseService;

    // CREATE
    @PostMapping
    public ResponseEntity<CourseResponseDto> createCourse(
            @Valid @RequestBody CourseRequestDto requestDto) {

        log.info("Received request to create course");

        CourseResponseDto created =
                courseService.createCourse(requestDto);

        log.info("Course created successfully with id: {}",
                created.getId());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }

    // GET ALL
    @GetMapping
    public ResponseEntity<List<CourseResponseDto>> getAllCourses() {

        log.info("Received request to retrieve all courses");

        List<CourseResponseDto> courses =
                courseService.getAllCourses();

        log.info("Successfully retrieved {} courses", courses.size());

        return ResponseEntity.ok(courses);
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<CourseResponseDto> getCourseById(
            @PathVariable Long id) {

        log.info("Received request to retrieve course with id: {}", id);

        CourseResponseDto course =
                courseService.getCourseById(id);

        log.info("Course retrieved successfully with id: {}", id);

        return ResponseEntity.ok(course);
    }

    // FULL UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<CourseResponseDto> updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody CourseRequestDto requestDto) {

        log.info("Received request to update course with id: {}", id);

        CourseResponseDto updated =
                courseService.updateCourse(id, requestDto);

        log.info("Course updated successfully with id: {}", id);

        return ResponseEntity.ok(updated);
    }

    // PARTIAL UPDATE
    @PatchMapping("/{id}")
    public ResponseEntity<CourseResponseDto> updateCoursePartially(
            @PathVariable Long id,
            @Valid @RequestBody CourseRequestPatchDto requestDto) {

        log.info("Received request to partially update course with id: {}",
                id);

        CourseResponseDto updated =
                courseService.updateCoursePartially(id, requestDto);

        log.info("Course partially updated successfully with id: {}", id);

        return ResponseEntity.ok(updated);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(
            @PathVariable Long id) {

        log.info("Received request to delete course with id: {}", id);

        courseService.deleteCourse(id);

        log.info("Course deleted successfully with id: {}", id);

        return ResponseEntity.noContent().build();
    }
}