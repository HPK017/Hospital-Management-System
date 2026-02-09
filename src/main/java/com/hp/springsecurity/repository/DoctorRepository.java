package com.hp.springsecurity.repository;

import com.hp.springsecurity.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
}
