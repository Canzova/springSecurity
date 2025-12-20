package com.codingshuttle.youtube.hospitalManagement.service;

import com.codingshuttle.youtube.hospitalManagement.dto.DoctorResponseDto;
import com.codingshuttle.youtube.hospitalManagement.dto.OnBoardNewDocRequestDTO;
import com.codingshuttle.youtube.hospitalManagement.dto.OnBoardNewDocResponseDTO;
import com.codingshuttle.youtube.hospitalManagement.entity.Doctor;
import com.codingshuttle.youtube.hospitalManagement.entity.User;
import com.codingshuttle.youtube.hospitalManagement.entity.type.RoleType;
import com.codingshuttle.youtube.hospitalManagement.exception.APIException;
import com.codingshuttle.youtube.hospitalManagement.repository.DoctorRepository;
import com.codingshuttle.youtube.hospitalManagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;

    public List<DoctorResponseDto> getAllDoctors() {
        return doctorRepository.findAll()
                .stream()
                .map(doctor -> modelMapper.map(doctor, DoctorResponseDto.class))
                .collect(Collectors.toList());
    }


    public OnBoardNewDocResponseDTO onBoardNewDoctor(OnBoardNewDocRequestDTO newDocDTO) {
        // Check if a user exists with the given id
        if(!userRepository.existsById(newDocDTO.getUserId())){
            log.info("User doesnot Exists....");
            throw new UsernameNotFoundException("User with " + newDocDTO.getUserId() + " does not exists.");
        }

        // If user exits and it is already a doctor
        User user = userRepository.findById(newDocDTO.getUserId()).orElseThrow();
        if(user.getRoles().contains(RoleType.DOCTOR)){
            log.info("User is already a doc");
            throw new IllegalArgumentException("This user id already a doctor.");
        }

        // Now we are sure that this user exits and it is not a doctor
        Doctor doctor = Doctor.builder()
                .name(newDocDTO.getName())
                .specialization(newDocDTO.getSpecialization())
                .email(user.getUsername())
                .user(user)
                .build();

        user.getRoles().add(RoleType.DOCTOR);
        doctor = doctorRepository.save(doctor);
        log.info("Doctor Created....");
        return modelMapper.map(doctor, OnBoardNewDocResponseDTO.class);
    }
}
