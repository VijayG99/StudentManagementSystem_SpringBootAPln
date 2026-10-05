package org.example.studentmanagementsystem.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.studentmanagementsystem.dto.Student.StudentRequestDto;
import org.example.studentmanagementsystem.dto.Student.StudentRequestPatchDto;
import org.example.studentmanagementsystem.dto.Student.StudentResponseDto;
import org.example.studentmanagementsystem.service.StudentService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@Slf4j
public class StudentController {

    private final StudentService studentService;

    //Creation API
    @CacheEvict(
            value = "students",
            allEntries = true
    )
    @PostMapping(
            value = "/create",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<StudentResponseDto> createStudent(
            @Valid @ModelAttribute StudentRequestDto requestDto) {

        log.info("Received request to create a new student");

        StudentResponseDto created =
                studentService.createStudent(requestDto);

        log.info("Student created successfully with id: {}", created.getId());

        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    //Retrieve Collection API
    @GetMapping("/getall")
    public ResponseEntity<Page<StudentResponseDto>> getAllStudents(
            @RequestParam(required = false) String search,
            Pageable pageable) {

        log.info("Search value from request: {}", search);

        Page<StudentResponseDto> students =
                studentService.getAllStudents(search, pageable);

        return ResponseEntity.ok(students);
    }

    //Retrieve Particular Data API
    @GetMapping("/{id}")
    public ResponseEntity<StudentResponseDto> getStudentById(
            @PathVariable Long id) {

        log.info("Received request to retrieve student with id: {}", id);

        StudentResponseDto student = studentService.getStudentById(id);

        log.info("Student retrieved successfully with id: {}", id);

        return ResponseEntity.ok(student);
    }

    //Update Operation API
    @CacheEvict(
            value = "students",
            allEntries = true
    )
    @PutMapping("/{id}")
    public ResponseEntity<StudentResponseDto> updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody StudentRequestDto requestDto) {

        log.info("Received request to update student with id: {}", id);

        StudentResponseDto updated = studentService.updateStudent(id, requestDto);

        log.info("Student updated successfully with id: {}", id);

        return ResponseEntity.ok(updated);
    }

    //Delete Operation API
    @CacheEvict(
            value = "students",
            allEntries = true
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(
            @PathVariable Long id) {

        log.info("Received request to delete student with id: {}", id);

        studentService.deleteStudent(id);

        log.info("Student deleted successfully with id: {}", id);

        return ResponseEntity.noContent().build();
    }

    //Partial Update API
    @PatchMapping("/{id}")
    public ResponseEntity<StudentResponseDto> updateStudentPartially(
            @PathVariable Long id,
            @Valid @RequestBody StudentRequestPatchDto request) {

        log.info("Received request to partially update student with id: {}", id);

        StudentResponseDto updated =
                studentService.updateStudentPartially(id, request);

        log.info("Student partially updated successfully with id: {}", id);

        return ResponseEntity.ok(updated);
    }

    //Retriving the Photo
    @GetMapping("/{studentId}/photo")
    public ResponseEntity<byte[]> getStudentPhoto(
            @PathVariable Long studentId) {

        byte[] photo = studentService.getStudentPhoto(studentId);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(photo);
    }

    //Assigning Departmnet
    @CacheEvict(
            value = "students",
            allEntries = true
    )
    @PutMapping("/{studentId}/department/{departmentId}")
    public ResponseEntity<String> assignDepartment(
            @PathVariable Long studentId,
            @PathVariable Long departmentId) {

        studentService.assignDepartment(studentId, departmentId);

        return ResponseEntity.ok(
                "Department assigned to student successfully"
        );
    }

    //Remove department
    @CacheEvict(
            value = "students",
            allEntries = true
    )
    @DeleteMapping("/{studentId}/department")
    public ResponseEntity<String> removeDepartment(
            @PathVariable Long studentId) {

        studentService.removeDepartment(studentId);

        return ResponseEntity.ok(
                "Department removed from student successfully"
        );
    }

    //Enrolling Course
    @CacheEvict(
            value = "students",
            allEntries = true
    )
    @PostMapping("/{studentId}/courses/{courseId}")
    public ResponseEntity<String> enrollCourse(
            @PathVariable Long studentId,
            @PathVariable Long courseId) {

        studentService.enrollCourse(studentId, courseId);

        return ResponseEntity.ok(
                "Course enrolled successfully"
        );
    }

    //Removing Course
    @CacheEvict(
            value = "students",
            allEntries = true
    )
    @DeleteMapping("/{studentId}/courses/{courseId}")
    public ResponseEntity<String> removeCourse(
            @PathVariable Long studentId,
            @PathVariable Long courseId) {

        studentService.removeCourse(studentId, courseId);

        return ResponseEntity.ok(
                "Course removed from student successfully"
        );
    }


    @GetMapping("/{studentId}/courses/ids")
    public ResponseEntity<List<Long>> getStudentCourseIds(
            @PathVariable Long studentId) {

        List<Long> courseIds =
                studentService.getStudentCourseIds(studentId);

        return ResponseEntity.ok(courseIds);
    }
}