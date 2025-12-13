package com.codingshuttle.youtube.hospitalManagement.security;

import com.codingshuttle.youtube.hospitalManagement.dto.LoginRequestDTO;
import com.codingshuttle.youtube.hospitalManagement.dto.LoginResponseDTO;
import com.codingshuttle.youtube.hospitalManagement.dto.SignUpResponseDTO;
import com.codingshuttle.youtube.hospitalManagement.entity.User;
import com.codingshuttle.youtube.hospitalManagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final AuthUtil authUtil;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    public LoginResponseDTO loginRequest(LoginRequestDTO loginRequestDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDTO.getUsername(), loginRequestDTO.getPassword())
        );

        User user = (User)authentication.getPrincipal();
        String jwtToken = authUtil.generateAccessToken(user);

        return new LoginResponseDTO(user.getId(), user.getUsername(), jwtToken);

    }

    // Here you need to create a user and save it into Db and return the saved user
    public SignUpResponseDTO signUpRequest(LoginRequestDTO signUpRequestDTO) {
        // Step 1 : Convert the DTO to entity type
        User user = modelMapper.map(signUpRequestDTO, User.class);

        // What if this user already exits
        if(userRepository.existsByUsername(user.getUsername())){
            throw new IllegalArgumentException("Username already exists");
        }

        // Encrypt the password before saving into db
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Step 2 : Now you can save this into db
        User savedUser = userRepository.save(user);

        // Step 3 : Return this SavedUser now into SignUpResponseDTO type
        return modelMapper.map(savedUser, SignUpResponseDTO.class);
    }
}
