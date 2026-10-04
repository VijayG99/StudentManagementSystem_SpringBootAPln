package org.example.studentmanagementsystem.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "course",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_course_code", columnNames = "code")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 20)
    private String code;

    @Column(length = 500)
    private String description;

    private Integer credits;

    // Many Courses belong to one Department
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    // Many Courses can have many Students
    @ManyToMany(mappedBy = "courses")
    @Builder.Default
    private Set<Student> students = new HashSet<>();

}