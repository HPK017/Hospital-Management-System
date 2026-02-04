package com.hp.springsecurity.dto;

import com.hp.springsecurity.type.BloodGroupType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PatientResponseDto {
    private Long id;
    private String name;
    private String gender;
    private LocalDateTime birthDate;
    private BloodGroupType bloodGroup;
}
