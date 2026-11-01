
package com.saludcomunitaria.app;

import com.saludcomunitaria.app.model.Paciente;
import com.saludcomunitaria.app.repository.PacienteRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class PacienteUserDetailsService implements UserDetailsService {

    private final PacienteRepository pacienteRepository;

    public PacienteUserDetailsService(
            PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String correo)
            throws UsernameNotFoundException {

        Paciente paciente = pacienteRepository.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "No existe un usuario con ese correo"));

        String rol = paciente.getRol();

        if (rol == null ||
                !(rol.equals("PACIENTE") ||
                  rol.equals("PROFESIONAL"))) {
            throw new UsernameNotFoundException(
                    "El usuario no tiene un rol válido");
        }

        return User.builder()
                .username(paciente.getCorreo())
                .password(paciente.getContrasenaHash())
                .roles(rol)
                .build();
    }
}
