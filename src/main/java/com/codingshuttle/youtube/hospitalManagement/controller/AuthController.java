package com.codingshuttle.youtube.hospitalManagement.controller;

import com.codingshuttle.youtube.hospitalManagement.dto.LoginRequestDTO;
import com.codingshuttle.youtube.hospitalManagement.dto.LoginResponseDTO;
import com.codingshuttle.youtube.hospitalManagement.dto.SignUpResponseDTO;
import com.codingshuttle.youtube.hospitalManagement.security.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> loginRequest(@RequestBody LoginRequestDTO loginRequestDTO){
        LoginResponseDTO loginResponseDTO = authService.loginRequest(loginRequestDTO);
        return new ResponseEntity<>(loginResponseDTO, HttpStatus.OK);
    }

    @PostMapping("/signup")
    public ResponseEntity<SignUpResponseDTO> signUpRequest(@RequestBody LoginRequestDTO signUpRequestDTO){
        SignUpResponseDTO signUpResponseDTO = authService.signUpRequest(signUpRequestDTO);
        return new ResponseEntity<>(signUpResponseDTO, HttpStatus.CREATED);
    }

}