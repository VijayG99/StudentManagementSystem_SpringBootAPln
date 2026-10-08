package org.example.studentmanagementsystem.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.example.studentmanagementsystem.common.BaseEntity;

/**
 * Deliberately minimal: a row here just PROVES a side effect happened
 * (no real email is sent). It's the stand-in for "notify the student"
 * in the approve/reject workflow, kept simple on purpose.
 */
@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String recipientEmail;

    @Column(nullable = false, length = 255)
    private String message;
}
