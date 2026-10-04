package org.example.studentmanagementsystem.service.Course;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.studentmanagementsystem.dto.Course.CourseResponseDto;
import org.example.studentmanagementsystem.dto.CourseRequestDto;
import org.example.studentmanagementsystem.dto.CourseRequestPatchDto;
import org.example.studentmanagementsystem.entity.Course;
import org.example.studentmanagementsystem.entity.Department;
import org.example.studentmanagementsystem.entity.Student;
import org.example.studentmanagementsystem.exception.DuplicateResourceException;
import org.example.studentmanagementsystem.exception.ResourceNotFoundException;
import org.example.studentmanagementsystem.repository.CourseRepository;
import org.example.studentmanagementsystem.repository.DepartmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final DepartmentRepository departmentRepository;

    // =========================================================
    // CREATE
    // =========================================================

    @Override
    @Transactional
    public CourseResponseDto createCourse(
            CourseRequestDto request) {

        log.info(
                "Creating course with code: {}",
                request.getCode()
        );

        if (courseRepository.existsByCode(request.getCode())) {

            throw new DuplicateResourceException(
                    "Course with code "
                            + request.getCode()
                            + " already exists."
            );
        }

        Department department =
                departmentRepository.findById(
                        request.getDepartmentId()
                ).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Department not found with id: "
                                        + request.getDepartmentId()
                        )
                );

        Course course = Course.builder()
                .name(request.getName())
                .code(request.getCode())
                .description(request.getDescription())
                .credits(request.getCredits())
                .department(department)
                .build();

        Course saved =
                courseRepository.save(course);

        log.info(
                "Course created successfully with id: {}",
                saved.getId()
        );

        return toResponse(saved);
    }

    // =========================================================
    // GET ALL
    // =========================================================

    @Override
    @Transactional
    public List<CourseResponseDto> getAllCourses() {

        return courseRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // =========================================================
    // GET BY ID
    // =========================================================

    @Override
    @Transactional
    public CourseResponseDto getCourseById(
            Long id) {

        return toResponse(
                getCourseEntity(id)
        );
    }

    // =========================================================
    // FULL UPDATE
    // =========================================================

    @Override
    @Transactional
    public CourseResponseDto updateCourse(
            Long id,
            CourseRequestDto request) {

        Course course =
                getCourseEntity(id);

        if (!course.getCode().equals(request.getCode())
                && courseRepository.existsByCode(
                request.getCode())) {

            throw new DuplicateResourceException(
                    "Course with code "
                            + request.getCode()
                            + " already exists."
            );
        }

        Department department =
                departmentRepository.findById(
                        request.getDepartmentId()
                ).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Department not found with id: "
                                        + request.getDepartmentId()
                        )
                );

        course.setName(request.getName());
        course.setCode(request.getCode());
        course.setDescription(request.getDescription());
        course.setCredits(request.getCredits());
        course.setDepartment(department);

        Course updated =
                courseRepository.save(course);

        return toResponse(updated);
    }

    // =========================================================
    // PATCH
    // =========================================================

    @Override
    @Transactional
    public CourseResponseDto updateCoursePartially(
            Long id,
            CourseRequestPatchDto request) {

        Course course =
                getCourseEntity(id);

        if (request.getName() != null) {
            course.setName(request.getName());
        }

        if (request.getCode() != null) {

            if (!course.getCode().equals(request.getCode())
                    && courseRepository.existsByCode(
                    request.getCode())) {

                throw new DuplicateResourceException(
                        "Course with code "
                                + request.getCode()
                                + " already exists."
                );
            }

            course.setCode(request.getCode());
        }

        if (request.getDescription() != null) {
            course.setDescription(
                    request.getDescription()
            );
        }

        if (request.getCredits() != null) {
            course.setCredits(
                    request.getCredits()
            );
        }

        if (request.getDepartmentId() != null) {

            Department department =
                    departmentRepository.findById(
                            request.getDepartmentId()
                    ).orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Department not found with id: "
                                            + request.getDepartmentId()
                            )
                    );

            course.setDepartment(department);
        }

        Course updated =
                courseRepository.save(course);

        return toResponse(updated);
    }

    // =========================================================
    // DELETE
    // =========================================================

    @Override
    @Transactional
    public void deleteCourse(Long id) {

        Course course =
                getCourseEntity(id);

        /*
         * Remove the course from students before
         * deleting it to avoid foreign-key problems
         * in the student_course join table.
         */
        for (Student student : course.getStudents()) {
            student.getCourses().remove(course);
        }

        courseRepository.delete(course);

        log.info(
                "Course deleted successfully with id: {}",
                id
        );
    }

    // =========================================================
    // GET STUDENT IDS
    // =========================================================

    @Override
    @Transactional
    public List<Long> getStudentIds(
            Long courseId) {

        Course course =
                getCourseEntity(courseId);

        return course.getStudents()
                .stream()
                .map(Student::getId)
                .collect(Collectors.toList());
    }

    // =========================================================
    // FIND ENTITY
    // =========================================================

    private Course getCourseEntity(Long id) {

        return courseRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Course not found with id: "
                                        + id
                        )
                );
    }

    // =========================================================
    // ENTITY -> DTO
    // =========================================================

    private CourseResponseDto toResponse(
            Course course) {

        return CourseResponseDto.builder()
                .id(course.getId())
                .name(course.getName())
                .code(course.getCode())
                .description(course.getDescription())
                .credits(course.getCredits())
                .departmentId(
                        course.getDepartment() != null
                                ? course.getDepartment().getId()
                                : null
                )
                .departmentName(
                        course.getDepartment() != null
                                ? course.getDepartment().getName()
                                : null
                )
                .studentIds(
                        course.getStudents()
                                .stream()
                                .map(Student::getId)
                                .collect(Collectors.toSet())
                )
                .build();
    }
}