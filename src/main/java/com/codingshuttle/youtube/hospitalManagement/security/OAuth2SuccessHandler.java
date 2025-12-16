package com.codingshuttle.youtube.hospitalManagement.security;

import com.codingshuttle.youtube.hospitalManagement.dto.LoginResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final AuthService authService;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // Step 1 : Get the OAuth2 Authentication token
        OAuth2AuthenticationToken authenticationToken = (OAuth2AuthenticationToken) authentication;

        // Step 2 : Get the user from this token
        OAuth2User oAuth2User = authenticationToken.getPrincipal();

        // Step 3 : Get the client registration id
        /*
                It can be google, github, meta, etc
         */

        String registrationId = authenticationToken.getAuthorizedClientRegistrationId();

        // Step 4 : Handle this Oauth2 login request
        ResponseEntity<LoginResponseDTO> loginResponse = authService.handleOAuth2LoginRequest(oAuth2User,
                registrationId);

        // Step 5 : Add this loginResponseDTO data into your response
        response.setStatus(loginResponse.getStatusCode().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(loginResponse.getBody()));
    }
}
