package org.example.studentmanagementsystem.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.studentmanagementsystem.dto.Student.StudentRequestDto;
import org.example.studentmanagementsystem.dto.Student.StudentRequestPatchDto;
import org.example.studentmanagementsystem.dto.Student.StudentResponseDto;
import org.example.studentmanagementsystem.entity.Course;
import org.example.studentmanagementsystem.entity.Department;
import org.example.studentmanagementsystem.entity.Student;
import org.example.studentmanagementsystem.exception.DuplicateResourceException;
import org.example.studentmanagementsystem.exception.ResourceNotFoundException;
import org.example.studentmanagementsystem.repository.CourseRepository;
import org.example.studentmanagementsystem.repository.DepartmentRepository;
import org.example.studentmanagementsystem.repository.StudentRepository;
import org.example.studentmanagementsystem.service.FileStorageService;
import org.example.studentmanagementsystem.service.StudentService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final DepartmentRepository departmentRepository;
    private final CourseRepository courseRepository;
    private final FileStorageService fileStorageService;

    // =========================================================
    // CREATE STUDENT
    // =========================================================

    @Override
    @Transactional
    public StudentResponseDto createStudent(StudentRequestDto request) {

        log.info("Creating new student with email: {}", request.getEmail());

        if (studentRepository.existsByEmail(request.getEmail())) {
            log.warn(
                    "Student creation failed. Email already exists: {}",
                    request.getEmail()
            );

            throw new DuplicateResourceException(
                    "Student with email "
                            + request.getEmail()
                            + " already exists."
            );
        }

        if (studentRepository.existsByPhone(request.getPhone())) {
            log.warn(
                    "Student creation failed. Phone already exists: {}",
                    request.getPhone()
            );

            throw new DuplicateResourceException(
                    "Student with phone "
                            + request.getPhone()
                            + " already exists."
            );
        }

        Student student = toEntity(request);

        /*
         * If departmentId is provided, find the department
         * and establish the relationship.
         */
        if (request.getDepartmentId() != null) {

            Department department = departmentRepository
                    .findById(request.getDepartmentId())
                    .orElseThrow(() -> {

                        log.warn(
                                "Department not found with id: {}",
                                request.getDepartmentId()
                        );

                        return new ResourceNotFoundException(
                                "Department not found with id: "
                                        + request.getDepartmentId()
                        );
                    });

            student.setDepartment(department);
        }

        Student savedStudent = studentRepository.save(student);

        log.info(
                "Student created successfully with id: {}",
                savedStudent.getId()
        );

        return toResponse(savedStudent);
    }

    // =========================================================
    // GET ALL STUDENTS
    // =========================================================


    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = "students",
            key = "'all:' + (#search == null ? '' : #search.trim()) + ':' + #pageable.pageNumber + ':' + #pageable.pageSize + ':' + #pageable.sort"
    )
    public Page<StudentResponseDto> getAllStudents(
            String search,
            Pageable pageable) {

        log.info("Search value received: {}", search);

        Page<Student> students;

        if (search == null || search.isBlank()) {

            log.info("No search value. Fetching all students.");

            students = studentRepository.findAll(pageable);

        } else {

            log.info("Search value present. Searching students: {}", search);

            students = studentRepository.searchStudents(
                    search.trim(),
                    pageable
            );
        }

        return students.map(this::toResponse);
    }

    // =========================================================
    // GET STUDENT BY ID
    // =========================================================

    @Cacheable(value = "students", key = "#id")
    @Override
    @Transactional
    public StudentResponseDto getStudentById(Long id) {

        log.info(
                "Fetching student with id: {}",
                id
        );

        Student student = getStudentEntity(id);

        return toResponse(student);
    }

    // =========================================================
    // FULL UPDATE
    // =========================================================

    @Override
    @Transactional
    public StudentResponseDto updateStudent(
            Long id,
            StudentRequestDto request) {

        log.info(
                "Updating student with id: {}",
                id
        );

        Student existingStudent = getStudentEntity(id);

        /*
         * Check email only if the new email belongs
         * to another student.
         */
        if (!existingStudent.getEmail().equals(request.getEmail())
                && studentRepository.existsByEmail(request.getEmail())) {

            throw new DuplicateResourceException(
                    "Student with email "
                            + request.getEmail()
                            + " already exists."
            );
        }

        /*
         * Check phone only if the new phone belongs
         * to another student.
         */
        if (!existingStudent.getPhone().equals(request.getPhone())
                && studentRepository.existsByPhone(request.getPhone())) {

            throw new DuplicateResourceException(
                    "Student with phone "
                            + request.getPhone()
                            + " already exists."
            );
        }

        existingStudent.setFirstName(request.getFirstName());
        existingStudent.setLastName(request.getLastName());
        existingStudent.setEmail(request.getEmail());
        existingStudent.setPhone(request.getPhone());
        existingStudent.setDateOfBirth(request.getDateOfBirth());

        /*
         * Update Department relationship.
         */
        if (request.getDepartmentId() != null) {

            Department department = departmentRepository
                    .findById(request.getDepartmentId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Department not found with id: "
                                            + request.getDepartmentId()
                            )
                    );

            existingStudent.setDepartment(department);

        } else {

            existingStudent.setDepartment(null);
        }

        Student updatedStudent =
                studentRepository.save(existingStudent);

        log.info(
                "Student updated successfully with id: {}",
                updatedStudent.getId()
        );

        return toResponse(updatedStudent);
    }

    // =========================================================
    // PARTIAL UPDATE
    // =========================================================

    @Override
    @Transactional
    public StudentResponseDto updateStudentPartially(
            Long id,
            StudentRequestPatchDto request) {

        log.info(
                "Partially updating student with id: {}",
                id
        );

        Student existingStudent = getStudentEntity(id);

        if (request.getFirstName() != null) {
            existingStudent.setFirstName(
                    request.getFirstName()
            );
        }

        if (request.getLastName() != null) {
            existingStudent.setLastName(
                    request.getLastName()
            );
        }

        /*
         * Email duplicate check.
         */
        if (request.getEmail() != null) {

            if (!existingStudent.getEmail()
                    .equals(request.getEmail())
                    && studentRepository.existsByEmail(
                    request.getEmail())) {

                throw new DuplicateResourceException(
                        "Student with email "
                                + request.getEmail()
                                + " already exists."
                );
            }

            existingStudent.setEmail(
                    request.getEmail()
            );
        }

        /*
         * Phone duplicate check.
         */
        if (request.getPhone() != null) {

            if (!existingStudent.getPhone()
                    .equals(request.getPhone())
                    && studentRepository.existsByPhone(
                    request.getPhone())) {

                throw new DuplicateResourceException(
                        "Student with phone "
                                + request.getPhone()
                                + " already exists."
                );
            }

            existingStudent.setPhone(
                    request.getPhone()
            );
        }

        if (request.getDateOfBirth() != null) {
            existingStudent.setDateOfBirth(
                    request.getDateOfBirth()
            );
        }

        /*
         * Update department only when departmentId
         * is supplied in PATCH.
         */
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

            existingStudent.setDepartment(department);
        }

        Student updatedStudent =
                studentRepository.save(existingStudent);

        log.info(
                "Student partially updated successfully with id: {}",
                updatedStudent.getId()
        );

        return toResponse(updatedStudent);
    }

    // =========================================================
    // DELETE STUDENT
    // =========================================================

    @Override
    @Transactional
    public void deleteStudent(Long id) {

        log.info(
                "Deleting student with id: {}",
                id
        );

        Student student = getStudentEntity(id);

        studentRepository.delete(student);

        log.info(
                "Student deleted successfully with id: {}",
                id
        );
    }

    // =========================================================
    // ASSIGN DEPARTMENT
    // =========================================================

    @Override
    @Transactional
    public void assignDepartment(
            Long studentId,
            Long departmentId) {

        log.info(
                "Assigning department {} to student {}",
                departmentId,
                studentId
        );

        Student student = getStudentEntity(studentId);

        Department department =
                departmentRepository.findById(departmentId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Department not found with id: "
                                                + departmentId
                                )
                        );

        student.setDepartment(department);

        studentRepository.save(student);

        log.info(
                "Department {} assigned to student {}",
                departmentId,
                studentId
        );
    }

    // =========================================================
    // REMOVE DEPARTMENT
    // =========================================================

    @Override
    @Transactional
    public void removeDepartment(Long studentId) {

        log.info(
                "Removing department from student {}",
                studentId
        );

        Student student = getStudentEntity(studentId);

        student.setDepartment(null);

        studentRepository.save(student);

        log.info(
                "Department removed from student {}",
                studentId
        );
    }

    // =========================================================
    // ENROLL COURSE
    // =========================================================

    @Override
    @Transactional
    public void enrollCourse(
            Long studentId,
            Long courseId) {

        log.info(
                "Enrolling student {} into course {}",
                studentId,
                courseId
        );

        Student student = getStudentEntity(studentId);

        Course course =
                courseRepository.findById(courseId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Course not found with id: "
                                                + courseId
                                )
                        );

        if (student.getCourses().contains(course)) {

            throw new DuplicateResourceException(
                    "Student is already enrolled in course "
                            + courseId
            );
        }

        student.getCourses().add(course);

        studentRepository.save(student);

        log.info(
                "Student {} successfully enrolled in course {}",
                studentId,
                courseId
        );
    }

    // =========================================================
    // REMOVE COURSE
    // =========================================================

    @Override
    @Transactional
    public void removeCourse(
            Long studentId,
            Long courseId) {

        log.info(
                "Removing course {} from student {}",
                courseId,
                studentId
        );

        Student student = getStudentEntity(studentId);

        Course course =
                courseRepository.findById(courseId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Course not found with id: "
                                                + courseId
                                )
                        );

        if (!student.getCourses().remove(course)) {

            throw new ResourceNotFoundException(
                    "Student is not enrolled in course "
                            + courseId
            );
        }

        studentRepository.save(student);

        log.info(
                "Course {} removed from student {}",
                courseId,
                studentId
        );
    }

    // =========================================================
    // GET STUDENT COURSE IDS
    // =========================================================

    @Override
    @Transactional
    public List<Long> getStudentCourseIds(
            Long studentId) {

        Student student = getStudentEntity(studentId);

        return student.getCourses()
                .stream()
                .map(Course::getId)
                .collect(Collectors.toList());
    }

    // =========================================================
    // FIND STUDENT ENTITY
    // =========================================================

    private Student getStudentEntity(Long id) {

        return studentRepository.findById(id)
                .orElseThrow(() -> {

                    log.warn(
                            "Student not found with id: {}",
                            id
                    );

                    return new ResourceNotFoundException(
                            "Student not found with id: " + id
                    );
                });
    }

    //For Uploading the Photo
    @Override
    @Transactional
    public void uploadPhoto(Long studentId, MultipartFile file) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student not found with id: " + studentId
                        ));

        String oldPhoto = student.getProfilePhoto();

        String filePath =
                fileStorageService.saveFile(file, "students");

        student.setProfilePhoto(filePath);

        studentRepository.save(student);

        if (oldPhoto != null && !oldPhoto.isBlank()) {
            fileStorageService.deleteFile(oldPhoto);
        }
    }

    //For Downloading the Photo
    @Override
    @Transactional(readOnly = true)
    public byte[] getStudentPhoto(Long studentId) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student not found with id: " + studentId
                        ));

        if (student.getProfilePhoto() == null ||
                student.getProfilePhoto().isBlank()) {
            throw new ResourceNotFoundException(
                    "Photo not found for student id: " + studentId
            );
        }

        return fileStorageService.getFile(student.getProfilePhoto());
    }

    // =========================================================
    // DTO -> ENTITY
    // =========================================================

    private Student toEntity(StudentRequestDto request) {

        return Student.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .dateOfBirth(request.getDateOfBirth())
                .build();
    }
    // =========================================================
    // ENTITY -> RESPONSE DTO
    // =========================================================

    private StudentResponseDto toResponse(Student student) {

        return StudentResponseDto.builder()
                .id(student.getId())
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .email(student.getEmail())
                .phone(student.getPhone())
                .dateOfBirth(student.getDateOfBirth())
                .profilePhoto(student.getProfilePhoto())
                .departmentId(
                        student.getDepartment() != null
                                ? student.getDepartment().getId()
                                : null
                )
                .departmentName(
                        student.getDepartment() != null
                                ? student.getDepartment().getName()
                                : null
                )
                .courseIds(
                        student.getCourses()
                                .stream()
                                .map(Course::getId)
                                .collect(Collectors.toSet())
                )
                .build();
    }


}