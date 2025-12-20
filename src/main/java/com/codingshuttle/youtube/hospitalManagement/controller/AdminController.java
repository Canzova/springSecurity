package com.codingshuttle.youtube.hospitalManagement.controller;

import com.codingshuttle.youtube.hospitalManagement.dto.OnBoardNewDocRequestDTO;
import com.codingshuttle.youtube.hospitalManagement.dto.OnBoardNewDocResponseDTO;
import com.codingshuttle.youtube.hospitalManagement.dto.PatientResponseDto;
import com.codingshuttle.youtube.hospitalManagement.service.DoctorService;
import com.codingshuttle.youtube.hospitalManagement.service.PatientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final PatientService patientService;
    private final DoctorService doctorService;

    @GetMapping("/patients")
    public ResponseEntity<List<PatientResponseDto>> getAllPatients(
            @RequestParam(value = "page", defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "size", defaultValue = "10") Integer pageSize
    ) {
        return ResponseEntity.ok(patientService.getAllPatients(pageNumber, pageSize));
    }

    @PostMapping("/onboardNewDoctor")
    public ResponseEntity<OnBoardNewDocResponseDTO> onBoardNewDoctor(@RequestBody OnBoardNewDocRequestDTO newDocDTO){
        log.info("In admin Controller");
        OnBoardNewDocResponseDTO newOnboardedDoc = doctorService.onBoardNewDoctor(newDocDTO);
        return new ResponseEntity<>(newOnboardedDoc, HttpStatus.CREATED);
    }
}
