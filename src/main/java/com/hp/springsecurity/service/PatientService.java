package com.hp.springsecurity.service;

import com.hp.springsecurity.dto.PatientResponseDto;
import com.hp.springsecurity.entity.Patient;
import com.hp.springsecurity.repository.PatientRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public PatientResponseDto getPatientById(Long patientId) {
        Patient patient = patientRepository.findById(patientId).
                orElseThrow(() -> new EntityNotFoundException("Patient not found with id: " + patientId));
        return modelMapper.map(patient, PatientResponseDto.class);
    }

    public List<PatientResponseDto> getAllPatients(Integer pageNumber, Integer pageSize) {
        return patientRepository.findAllPatients(PageRequest.of(pageNumber, pageSize))
                .stream()
                .map(patient -> modelMapper.map(patient, PatientResponseDto.class))
                .collect(Collectors.toList());
    }
}
