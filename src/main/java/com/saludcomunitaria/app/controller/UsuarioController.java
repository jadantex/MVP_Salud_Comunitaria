
package com.saludcomunitaria.app.controller;

import com.saludcomunitaria.app.model.Paciente;
import com.saludcomunitaria.app.repository.PacienteRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UsuarioController {

    private final PacienteRepository pacienteRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioController(
            PacienteRepository pacienteRepository,
            PasswordEncoder passwordEncoder) {
        this.pacienteRepository = pacienteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/registro")
    public String mostrarRegistro() {
        return "registro";
    }

    @PostMapping("/registro")
    public String registrarUsuario(
            @RequestParam String nombre,
            @RequestParam String correo,
            @RequestParam String contrasena,
            @RequestParam String confirmarContrasena,
            Model model) {

        nombre = nombre.trim();
        correo = correo.trim().toLowerCase();

        if (nombre.isBlank() || correo.isBlank()
                || contrasena.isBlank()
                || confirmarContrasena.isBlank()) {
            model.addAttribute(
                    "error", "Todos los campos son obligatorios.");
            return "registro";
        }

        if (!contrasena.equals(confirmarContrasena)) {
            model.addAttribute(
                    "error", "Las contraseñas no coinciden.");
            return "registro";
        }

        if (contrasena.length() < 8) {
            model.addAttribute(
                    "error",
                    "La contraseña debe tener al menos 8 caracteres.");
            return "registro";
        }

        if (pacienteRepository.findByCorreo(correo).isPresent()) {
            model.addAttribute(
                    "error", "Este correo ya está registrado.");
            return "registro";
        }

        Paciente paciente = new Paciente();
        paciente.setNombre(nombre);
        paciente.setCorreo(correo);
        paciente.setContrasenaHash(
                passwordEncoder.encode(contrasena));

        // Todo usuario registrado públicamente será paciente.
        paciente.setRol("PACIENTE");

        pacienteRepository.save(paciente);

        return "redirect:/?registro=ok";
    }
}
