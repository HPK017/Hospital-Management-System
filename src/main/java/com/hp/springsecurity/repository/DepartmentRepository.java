package com.hp.springsecurity.repository;

import com.hp.springsecurity.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
}
