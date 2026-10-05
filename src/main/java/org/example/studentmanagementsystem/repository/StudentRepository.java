package org.example.studentmanagementsystem.repository;

import org.example.studentmanagementsystem.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student,Long> {

    Optional<Student> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    Page<Student> findAll(Pageable pageable);

    @Query("""
    SELECT DISTINCT s
    FROM Student s
    LEFT JOIN s.department d
    LEFT JOIN s.courses c
    WHERE
           LOWER(s.firstName) LIKE LOWER(CONCAT('%', :search, '%'))
        OR LOWER(s.lastName) LIKE LOWER(CONCAT('%', :search, '%'))
        OR LOWER(s.email) LIKE LOWER(CONCAT('%', :search, '%'))
        OR s.phone LIKE CONCAT('%', :search, '%')
        OR LOWER(d.name) LIKE LOWER(CONCAT('%', :search, '%'))
        OR LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%'))
        OR LOWER(c.code) LIKE LOWER(CONCAT('%', :search, '%'))
""")
    Page<Student> searchStudents(
            @Param("search") String search,
            Pageable pageable
    );
}
