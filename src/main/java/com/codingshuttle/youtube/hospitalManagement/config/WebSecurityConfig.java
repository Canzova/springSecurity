package com.codingshuttle.youtube.hospitalManagement.config;

import com.codingshuttle.youtube.hospitalManagement.entity.type.RoleType;
import com.codingshuttle.youtube.hospitalManagement.security.JwtAuthFilter;
import com.codingshuttle.youtube.hospitalManagement.security.OAuth2SuccessHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import static com.codingshuttle.youtube.hospitalManagement.entity.type.PermissionType.APPOINTMENT_DELETE;
import static com.codingshuttle.youtube.hospitalManagement.entity.type.PermissionType.USER_MANAGE;


@Configuration
@RequiredArgsConstructor
@Slf4j
@EnableMethodSecurity
public class WebSecurityConfig {

    private final PasswordEncoder passwordEncoder;
    private final JwtAuthFilter jwtAuthFilter;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final HandlerExceptionResolver handlerExceptionResolver;

    // I am defining my own rules, how spring security should authenticate any request
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(csrf->csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth->
                    auth.requestMatchers("/public/**", "/auth/**").permitAll()
                            .requestMatchers("/admin/**").hasRole(RoleType.ADMIN.name())
                            .requestMatchers("/doctors/**").hasAnyRole(RoleType.ADMIN.name(), RoleType.DOCTOR.name())
                            .requestMatchers(HttpMethod.DELETE, "/admin/**")
                            .hasAnyAuthority(APPOINTMENT_DELETE.name(),
                                    USER_MANAGE.name())
                            .requestMatchers("/h2-console/**").permitAll()
                            .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .oauth2Login(oAuth2->
                        oAuth2.failureHandler((request, response, exception)->{
                        log.error("OAuth2 error: {}", exception.getMessage());
                                    handlerExceptionResolver.resolveException(request, response, null, exception);
                }).successHandler(oAuth2SuccessHandler)
                )
                .exceptionHandling(exceptionHandlingConfigurer ->
                    exceptionHandlingConfigurer.accessDeniedHandler((request, response, accessDeniedException) -> {
                        handlerExceptionResolver.resolveException(request, response, null, accessDeniedException);

                    }));


//                .formLogin(Customizer.withDefaults());
        httpSecurity.headers(header->header.frameOptions(
                frameOptions -> frameOptions.sameOrigin()
        ));

        return httpSecurity.build();
    }



    /*
    // Creating my own in memory user
    @Bean
    public UserDetailsService userDetailsService(){
        UserDetails user1 = User.withUsername("Nihal")
                .password(passwordEncoder.encode("pass"))
                .roles("ADMIN")
                .build();

        UserDetails user2 = User.withUsername("Pawan")
                .password(passwordEncoder.encode("pass"))
                .roles("DOCTOR")
                .build();

        // InMemoryUserDetailsManager implements userDetailsManager
        return new InMemoryUserDetailsManager(user1, user2);
    }

     */
}
