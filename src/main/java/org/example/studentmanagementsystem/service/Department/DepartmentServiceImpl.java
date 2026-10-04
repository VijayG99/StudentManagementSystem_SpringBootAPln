package org.example.studentmanagementsystem.service.Department;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.studentmanagementsystem.dto.DepartmentRequestDto;
import org.example.studentmanagementsystem.dto.DepartmentRequestPatchDto;
import org.example.studentmanagementsystem.dto.DepartmentResponseDto;
import org.example.studentmanagementsystem.entity.Course;
import org.example.studentmanagementsystem.entity.Department;
import org.example.studentmanagementsystem.entity.Student;
import org.example.studentmanagementsystem.exception.DuplicateResourceException;
import org.example.studentmanagementsystem.exception.ResourceNotFoundException;
import org.example.studentmanagementsystem.repository.CourseRepository;
import org.example.studentmanagementsystem.repository.DepartmentRepository;
import org.example.studentmanagementsystem.service.Department.DepartmentService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final CourseRepository courseRepository;

    // =========================================================
    // CREATE
    // =========================================================

    @Override
    @Transactional
    public DepartmentResponseDto createDepartment(
            DepartmentRequestDto request) {

        log.info(
                "Creating department with code: {}",
                request.getCode()
        );

        if (departmentRepository.existsByCode(request.getCode())) {

            throw new DuplicateResourceException(
                    "Department with code "
                            + request.getCode()
                            + " already exists."
            );
        }

        Department department = Department.builder()
                .name(request.getName())
                .code(request.getCode())
                .description(request.getDescription())
                .build();

        Department saved =
                departmentRepository.save(department);

        log.info(
                "Department created successfully with id: {}",
                saved.getId()
        );

        return toResponse(saved);
    }

    // =========================================================
    // GET ALL
    // =========================================================

    @Override
    @Transactional
    public List<DepartmentResponseDto> getAllDepartments() {

        return departmentRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // =========================================================
    // GET BY ID
    // =========================================================

    @Override
    @Transactional
    public DepartmentResponseDto getDepartmentById(
            Long id) {

        return toResponse(
                getDepartmentEntity(id)
        );
    }

    // =========================================================
    // FULL UPDATE
    // =========================================================

    @Override
    @Transactional
    public DepartmentResponseDto updateDepartment(
            Long id,
            DepartmentRequestDto request) {

        Department department =
                getDepartmentEntity(id);

        if (!department.getCode().equals(request.getCode())
                && departmentRepository.existsByCode(
                request.getCode())) {

            throw new DuplicateResourceException(
                    "Department with code "
                            + request.getCode()
                            + " already exists."
            );
        }

        department.setName(request.getName());
        department.setCode(request.getCode());
        department.setDescription(request.getDescription());

        Department updated =
                departmentRepository.save(department);

        return toResponse(updated);
    }

    // =========================================================
    // PATCH
    // =========================================================

    @Override
    @Transactional
    public DepartmentResponseDto updateDepartmentPartially(
            Long id,
            DepartmentRequestPatchDto request) {

        Department department =
                getDepartmentEntity(id);

        if (request.getName() != null) {
            department.setName(request.getName());
        }

        if (request.getCode() != null) {

            if (!department.getCode().equals(request.getCode())
                    && departmentRepository.existsByCode(
                    request.getCode())) {

                throw new DuplicateResourceException(
                        "Department with code "
                                + request.getCode()
                                + " already exists."
                );
            }

            department.setCode(request.getCode());
        }

        if (request.getDescription() != null) {
            department.setDescription(
                    request.getDescription()
            );
        }

        Department updated =
                departmentRepository.save(department);

        return toResponse(updated);
    }

    // =========================================================
    // DELETE
    // =========================================================

    @Override
    @Transactional
    public void deleteDepartment(Long id) {

        Department department =
                getDepartmentEntity(id);

        departmentRepository.delete(department);

        log.info(
                "Department deleted successfully with id: {}",
                id
        );
    }

    // =========================================================
    // GET STUDENT IDS
    // =========================================================

    @Override
    @Transactional
    public List<Long> getStudentIds(
            Long departmentId) {

        Department department =
                getDepartmentEntity(departmentId);

        return department.getStudents()
                .stream()
                .map(Student::getId)
                .collect(Collectors.toList());
    }

    // =========================================================
    // GET COURSE IDS
    // =========================================================

    @Override
    @Transactional
    public List<Long> getCourseIds(
            Long departmentId) {

        Department department =
                getDepartmentEntity(departmentId);

        return department.getCourses()
                .stream()
                .map(Course::getId)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void removeCourseFromDepartment(Long departmentId, Long courseId) {

        log.info("Removing course {} from department {}", courseId, departmentId);

        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Department not found with id: " + departmentId));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Course not found with id: " + courseId));

        if (!department.getCourses().contains(course)) {
            throw new ResourceNotFoundException(
                    "Course " + courseId +
                            " does not belong to department " + departmentId);
        }

        // Remove course from all students first
        for (Student student : course.getStudents()) {
            student.getCourses().remove(course);
        }

        // Clear the inverse side
        course.getStudents().clear();

        // Remove course from department
        // orphanRemoval = true will delete the Course
        department.removeCourse(course);

        log.info(
                "Course {} removed from department {}. " +
                        "Orphan removal will delete the course.",
                courseId,
                departmentId
        );
    }
    // =========================================================
    // FIND ENTITY
    // =========================================================

    private Department getDepartmentEntity(Long id) {

        return departmentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Department not found with id: "
                                        + id
                        )
                );
    }

    // =========================================================
    // ENTITY -> DTO
    // =========================================================

    private DepartmentResponseDto toResponse(
            Department department) {

        return DepartmentResponseDto.builder()
                .id(department.getId())
                .name(department.getName())
                .code(department.getCode())
                .description(department.getDescription())
                .studentIds(
                        department.getStudents()
                                .stream()
                                .map(Student::getId)
                                .collect(Collectors.toList())
                )
                .courseIds(
                        department.getCourses()
                                .stream()
                                .map(Course::getId)
                                .collect(Collectors.toList())
                )
                .build();
    }
}