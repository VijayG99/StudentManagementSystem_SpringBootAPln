package org.example.studentmanagementsystem.service.Impl;



import lombok.RequiredArgsConstructor;
import org.example.studentmanagementsystem.dto.enrollment.EnrollmentRequestDto;
import org.example.studentmanagementsystem.dto.enrollment.EnrollmentResponseDto;
import org.example.studentmanagementsystem.entity.*;
import org.example.studentmanagementsystem.exception.CourseFullException;
import org.example.studentmanagementsystem.exception.InvalidRequestStateException;
import org.example.studentmanagementsystem.exception.ResourceNotFoundException;
import org.example.studentmanagementsystem.repository.CourseRepository;
import org.example.studentmanagementsystem.repository.EnrollmentRequestRepository;
import org.example.studentmanagementsystem.repository.NotificationRepository;
import org.example.studentmanagementsystem.repository.StudentRepository;
import org.example.studentmanagementsystem.service.EnrollmentRequestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * This class is the teaching centerpiece for "multiple repository saves
 * in one transaction": approve() writes to FOUR different repositories
 * (course, student, enrollmentRequest, notification) inside a single
 * @Transactional method. If any step fails, every save made earlier in
 * the SAME method call is rolled back — nothing is left half-done.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EnrollmentRequestServiceImpl implements EnrollmentRequestService {

    private static final Logger log = LoggerFactory.getLogger(EnrollmentRequestServiceImpl.class);

    private final EnrollmentRequestRepository enrollmentRequestRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final NotificationRepository notificationRepository;

    @Override
    @Transactional
    public EnrollmentResponseDto apply(EnrollmentRequestDto request) {
        log.info("Student {} applying for course {}", request.getStudentId(), request.getCourseId());

        Student student = findStudent(request.getStudentId());
        Course course = findCourse(request.getCourseId());

        EnrollmentRequest enrollmentRequest = EnrollmentRequest.builder()
                .student(student)
                .course(course)
                .status(EnrollmentStatus.PENDING)
                .build();

        EnrollmentRequest saved = enrollmentRequestRepository.save(enrollmentRequest);
        log.info("Enrollment request created with id: {}", saved.getId());
        return toResponse(saved);
    }

    @Override
    public EnrollmentResponseDto getById(Long id) {
        return toResponse(findRequest(id));
    }

    @Override
    public List<EnrollmentResponseDto> getAll() {
        return enrollmentRequestRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public EnrollmentResponseDto approve(Long id) {
        EnrollmentRequest enrollmentRequest = findRequest(id);
        requirePending(enrollmentRequest);

        Course course = enrollmentRequest.getCourse();
        Student student = enrollmentRequest.getStudent();

        // --- Save #1: course, after the capacity check (the natural rollback trigger) ---
        if (course.getEnrolledCount() >= course.getCapacity()) {
            log.warn("Course {} is full ({}/{})", course.getCode(), course.getEnrolledCount(), course.getCapacity());
            throw new CourseFullException("Course is full: " + course.getName());
        }
        course.setEnrolledCount(course.getEnrolledCount() + 1);
        courseRepository.save(course);

        // --- Save #2: student, actually enrolled now ---
        student.getCourses().add(course);
        studentRepository.save(student);

        // --- Save #3: the request itself, marked approved ---
        enrollmentRequest.setStatus(EnrollmentStatus.APPROVED);
        EnrollmentRequest updatedRequest = enrollmentRequestRepository.save(enrollmentRequest);

        // --- Save #4: a notification proving the student was told ---
        notificationRepository.save(Notification.builder()
                .recipientEmail(student.getEmail())
                .message("Your enrollment in " + course.getName() + " has been approved.")
                .build());

        log.info("Enrollment request {} approved: student {} -> course {}",
                id, student.getId(), course.getId());

        return toResponse(updatedRequest);
    }

    @Override
    @Transactional
    public EnrollmentResponseDto reject(Long id) {
        EnrollmentRequest enrollmentRequest = findRequest(id);
        requirePending(enrollmentRequest);

        // Nothing was ever incremented for a PENDING request, so rejecting
        // only needs two saves — a deliberate contrast with approve()'s four.
        enrollmentRequest.setStatus(EnrollmentStatus.REJECTED);
        EnrollmentRequest updatedRequest = enrollmentRequestRepository.save(enrollmentRequest);

        notificationRepository.save(Notification.builder()
                .recipientEmail(enrollmentRequest.getStudent().getEmail())
                .message("Your enrollment in " + enrollmentRequest.getCourse().getName()+ " was rejected.")
                .build());

        log.info("Enrollment request {} rejected", id);
        return toResponse(updatedRequest);
    }

    // ---------- helpers ----------

    private void requirePending(EnrollmentRequest request) {
        if (request.getStatus() != EnrollmentStatus.PENDING) {
            throw new InvalidRequestStateException(
                    "Enrollment request " + request.getId() + " has already been " + request.getStatus());
        }
    }

    private EnrollmentRequest findRequest(Long id) {
        return enrollmentRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment request not found with id: " + id));
    }

    private Student findStudent(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
    }

    private Course findCourse(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
    }

    private EnrollmentResponseDto toResponse(EnrollmentRequest request) {
        return EnrollmentResponseDto.builder()
                .id(request.getId())
                .studentId(request.getStudent().getId())
                .studentName(request.getStudent().getFirstName() + " " + request.getStudent().getLastName())
                .courseId(request.getCourse().getId())
                .courseTitle(request.getCourse().getName())
                .status(request.getStatus())
                .requestedAt(request.getCreatedAt())
                .build();
    }
}
