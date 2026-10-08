package org.example.studentmanagementsystem.entity;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.example.studentmanagementsystem.common.BaseEntity;

@Entity
@Table(name = "enrollment_requests")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class EnrollmentRequest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    // createdAt (from BaseEntity) already tells us when this was requested,
    // so no separate "requestedAt" field is needed here.
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EnrollmentStatus status = EnrollmentStatus.PENDING;
}
