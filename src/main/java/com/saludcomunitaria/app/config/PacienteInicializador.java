
package com.saludcomunitaria.app.config;

import com.saludcomunitaria.app.model.Paciente;
import com.saludcomunitaria.app.repository.PacienteRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PacienteInicializador {

    @Bean
    public CommandLineRunner crearPacientePrueba(
            PacienteRepository pacienteRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            String correo = "paciente@salud.com";

            if (pacienteRepository.findByCorreo(correo).isEmpty()) {

                Paciente paciente = new Paciente();

                paciente.setNombre("Paciente de prueba");
                paciente.setCorreo(correo);
                paciente.setContrasenaHash(
                    passwordEncoder.encode("Paciente123!")
                );

                pacienteRepository.save(paciente);

                System.out.println(
                    "Paciente de prueba creado correctamente."
                );
            }
        };
    }
}
