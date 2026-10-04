package org.example.studentmanagementsystem.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "department",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_department_code", columnNames = "code")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 20)
    private String code;

    @Column(length = 500)
    private String description;

    // One Department has many Students
    @OneToMany(mappedBy = "department")

    @Builder.Default
    private List<Student> students = new ArrayList<>();

    // One Department offers many Courses
    @OneToMany(mappedBy = "department",
    cascade = CascadeType.ALL,
    orphanRemoval = true
    )
    @Builder.Default
    private List<Course> courses = new ArrayList<>();

    public void addCourse(Course course) {
        courses.add(course);
        course.setDepartment(this);
    }

    public void removeCourse(Course course) {
        courses.remove(course);
        course.setDepartment(null);
    }
}