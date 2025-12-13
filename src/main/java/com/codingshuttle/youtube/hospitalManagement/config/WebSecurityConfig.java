package com.codingshuttle.youtube.hospitalManagement.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final PasswordEncoder passwordEncoder;

    // I am defining my own rules, how spring security should authenticate any request
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(csrf->csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth->
                    auth.requestMatchers("/public/**", "/auth/**").permitAll()
                            .requestMatchers("/admin/**").hasRole("ADMIN")
                            .requestMatchers("/doctors/**").hasAnyRole("ADMIN", "DOCTOR")

                );
//                .formLogin(Customizer.withDefaults());

        return httpSecurity.build();
    }


    // This is required because In Spring Security 5.7+, AuthenticationManager is no longer auto-exposed as a bean.
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
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
