package org.example.studentmanagementsystem.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.studentmanagementsystem.dto.Student.StudentRequestDto;
import org.example.studentmanagementsystem.dto.Student.StudentRequestPatchDto;
import org.example.studentmanagementsystem.dto.Student.StudentResponseDto;
import org.example.studentmanagementsystem.service.Student.StudentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@Slf4j
public class StudentController {

    private final StudentService studentService;

    //Creation API
    @PostMapping("/create")
    public ResponseEntity<StudentResponseDto> createStudent(
            @Valid @RequestBody StudentRequestDto requestDto) {

        log.info("Received request to create a new student");

        StudentResponseDto created = studentService.createStudent(requestDto);

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

    //Upload Photo
    @PostMapping("/{studentId}/photo")
    public ResponseEntity<String> uploadPhoto(
            @PathVariable Long studentId,
            @RequestParam("file") MultipartFile file) {

        studentService.uploadPhoto(studentId, file);

        return ResponseEntity.ok("Student photo uploaded successfully");
    }

    @GetMapping("/{studentId}/photo")
    public ResponseEntity<byte[]> getStudentPhoto(
            @PathVariable Long studentId) {

        byte[] photo = studentService.getStudentPhoto(studentId);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(photo);
    }
}