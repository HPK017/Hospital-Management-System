package com.hp.springsecurity.entity;

import com.hp.springsecurity.type.BloodGroupType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(
        name = "patient",
        uniqueConstraints = {
                @UniqueConstraint(name = "unique_patient_name_birthdate",
                        columnNames = {"name", "birthDate"})
        },
        indexes = {
                @Index(name = "idx_patient_birth_date", columnList = "birthDate")
        }
)
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 40)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    private LocalDate birthDate;

    private String gender;

//    @OneToOne
//    @MapsId
//    private User user;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private BloodGroupType bloodGroup;

    @OneToOne(cascade = {CascadeType.ALL}, orphanRemoval = true)
    @JoinColumn(name = "patient_insurance_id")
    private Insurance insurance;

    @OneToMany(mappedBy = "patient", cascade = {CascadeType.REMOVE},
            orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Appointment> appointments = new ArrayList<>();

}
