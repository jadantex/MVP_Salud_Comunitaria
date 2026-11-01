package com.saludcomunitaria.app;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.http.HttpMethod;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/registro",
                                "/css/**",
                                "/error")
                        .permitAll()
                        .requestMatchers(
                                HttpMethod.POST,
                                "/citas/atender")
                        .hasRole("PROFESIONAL")
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/")
                        .loginProcessingUrl("/login")
                        .usernameParameter("correo")
                        .passwordParameter("contrasena")
                        .defaultSuccessUrl("/paciente", true)
                        .failureUrl("/?error")
                        .permitAll())
                .logout(logout -> logout
                        .logoutSuccessUrl("/?logout")
                        .permitAll());

        return http.build();
    }
}