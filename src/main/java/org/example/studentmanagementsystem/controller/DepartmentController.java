package org.example.studentmanagementsystem.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.studentmanagementsystem.dto.DepartmentRequestDto;
import org.example.studentmanagementsystem.dto.DepartmentRequestPatchDto;
import org.example.studentmanagementsystem.dto.DepartmentResponseDto;
import org.example.studentmanagementsystem.service.Department.DepartmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
@Slf4j
public class DepartmentController {

    private final DepartmentService departmentService;

    // CREATE
    @PostMapping
    public ResponseEntity<DepartmentResponseDto> createDepartment(
            @Valid @RequestBody DepartmentRequestDto requestDto) {

        log.info("Received request to create department");

        DepartmentResponseDto created =
                departmentService.createDepartment(requestDto);

        log.info("Department created successfully with id: {}",
                created.getId());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }

    // GET ALL
    @GetMapping
    public ResponseEntity<List<DepartmentResponseDto>> getAllDepartments() {

        log.info("Received request to retrieve all departments");

        List<DepartmentResponseDto> departments =
                departmentService.getAllDepartments();

        log.info("Successfully retrieved {} departments",
                departments.size());

        return ResponseEntity.ok(departments);
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<DepartmentResponseDto> getDepartmentById(
            @PathVariable Long id) {

        log.info("Received request to retrieve department with id: {}", id);

        DepartmentResponseDto department =
                departmentService.getDepartmentById(id);

        log.info("Department retrieved successfully with id: {}", id);

        return ResponseEntity.ok(department);
    }

    // FULL UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<DepartmentResponseDto> updateDepartment(
            @PathVariable Long id,
            @Valid @RequestBody DepartmentRequestDto requestDto) {

        log.info("Received request to update department with id: {}", id);

        DepartmentResponseDto updated =
                departmentService.updateDepartment(id, requestDto);

        log.info("Department updated successfully with id: {}", id);

        return ResponseEntity.ok(updated);
    }

    // PARTIAL UPDATE
    @PatchMapping("/{id}")
    public ResponseEntity<DepartmentResponseDto> updateDepartmentPartially(
            @PathVariable Long id,
            @Valid @RequestBody DepartmentRequestPatchDto requestDto) {

        log.info(
                "Received request to partially update department with id: {}",
                id
        );

        DepartmentResponseDto updated =
                departmentService.updateDepartmentPartially(id, requestDto);

        log.info(
                "Department partially updated successfully with id: {}",
                id
        );

        return ResponseEntity.ok(updated);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepartment(
            @PathVariable Long id) {

        log.info("Received request to delete department with id: {}", id);

        departmentService.deleteDepartment(id);

        log.info("Department deleted successfully with id: {}", id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{departmentId}/courses")
    public ResponseEntity<List<Long>> getCourseIds(
            @PathVariable Long departmentId) {

        return ResponseEntity.ok(
                departmentService.getCourseIds(departmentId)
        );
    }
    //Orphan Removal
    @DeleteMapping("/{departmentId}/courses/{courseId}")
    public ResponseEntity<Void> removeCourseFromDepartment(
            @PathVariable Long departmentId,
            @PathVariable Long courseId) {

        departmentService.removeCourseFromDepartment(
                departmentId,
                courseId
        );

        return ResponseEntity.noContent().build();
    }
}