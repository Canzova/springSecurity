package com.codingshuttle.youtube.hospitalManagement.security;

import com.codingshuttle.youtube.hospitalManagement.dto.LoginRequestDTO;
import com.codingshuttle.youtube.hospitalManagement.dto.LoginResponseDTO;
import com.codingshuttle.youtube.hospitalManagement.dto.SignUpResponseDTO;
import com.codingshuttle.youtube.hospitalManagement.entity.User;
import com.codingshuttle.youtube.hospitalManagement.entity.type.AuthProviderType;
import com.codingshuttle.youtube.hospitalManagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
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
        User savedUser = signInInternal(signUpRequestDTO, null, AuthProviderType.EMAIL);

        // Step 3 : Return this SavedUser now into SignUpResponseDTO type
        return modelMapper.map(savedUser, SignUpResponseDTO.class);
    }

    User signInInternal(LoginRequestDTO signUpRequestDTO, String providerId, AuthProviderType authProviderType){
        User user = modelMapper.map(signUpRequestDTO, User.class);

        // What if this user already exits
        if(userRepository.existsByUsername(user.getUsername())){
            throw new IllegalArgumentException("Username already exists");
        }

        // Encrypt the password before saving into db, if user is signing up from email and password
        if(authProviderType.equals(AuthProviderType.EMAIL))  user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setProviderId(providerId);
        user.setProviderType(authProviderType);


        // Step 2 : Now you can save this into db

        return userRepository.save(user);
    }

    public ResponseEntity<LoginResponseDTO> handleOAuth2LoginRequest(OAuth2User oAuth2User, String registrationId) {

        // Step 1 : Get  providerType --->
        /*
        registrationId ---> google ----> Into Enum ----> GOOGle
         registrationId ---> github ----> Into Enum ----> GITHUB
         */

        AuthProviderType providerType = authUtil.getProviderTypeFromRegistrationId(registrationId);

        // Step 2 : Get the provider id ---> A unique id provided by different providers like google or github
        String providerId = authUtil.determineProviderIdFromOAuth2User(oAuth2User, registrationId);

        // Step 3 : Check if user has already created account with this providerType and provideId
        User user = userRepository.findByProviderTypeAndProviderId(providerType, providerId);

        // Step 4 : Try to get the mail id from the auth2 provider, if it provides the mail id
        // SOme providers does not give the email
        String email = oAuth2User.getAttribute("email");

        // Step 5 : Get the user from this email from db
        /*

            -> We are assuming that we will save the username as email if user is registering from OAuth2 provider
            -> So now just check if you already have a user with this email, it means that user have already
                created account with this email (by any other oAuth provider) and again trying to Create an account
                with different Oauth2 provider

            -> In that case you don;t need to register the user, ask him to login with respective oAuth2 provider

         */

        User emailUser = userRepository.findByUsername(email).orElse(null);

        if(user == null && emailUser == null) {

            // Generate the user Name
            /*
                if you have the email then email will be the userName
                else you will create a unique username using providerType and providerId
             */

            String username = authUtil.determineUsernameFromOAuth2User(oAuth2User, registrationId, providerId);
            // This is a new user----> DO sign in
            user = signInInternal(new LoginRequestDTO(username, null), providerId, providerType);
        }
        else if(user != null){
            /*
               User with their providerId and AUth Provider type already exits,

                we are not storing the email as username everytime, if email is not present into token then we were storing just a unique id
                May be this time user has given us the access to his/her mail id

                So just chek if in the username you don't have this email, means in the username you have stored just unique id and not actual email
                So update it to email
             */

            if(email != null && !email.isBlank() && !email.equals(user.getUsername())){
                user.setUsername(email);
                userRepository.save(user);
            }
        }
        else
        {
            /*
                user == null and emailUser != null
                When user has not created an account with this authProvider but with any other authProvider
                So ask him to login with that auth provider only
             */

            throw new BadCredentialsException("This email is already registered with provider "+emailUser.getProviderType());
        }

        // user != null && emailUser != null means you have registered same user twice, So this case will not some

        // Step 6 : Return the loginResponse
        LoginResponseDTO loginResponseDto = new LoginResponseDTO(user.getId(),user.getUsername(), authUtil.generateAccessToken(user));
        return ResponseEntity.ok(loginResponseDto);
    }
}
