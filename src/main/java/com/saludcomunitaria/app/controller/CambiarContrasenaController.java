package com.saludcomunitaria.app.controller;

import com.saludcomunitaria.app.model.Paciente;
import com.saludcomunitaria.app.repository.PacienteRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CambiarContrasenaController {

    private final PacienteRepository pacienteRepository;
    private final PasswordEncoder passwordEncoder;

    public CambiarContrasenaController(
            PacienteRepository pacienteRepository,
            PasswordEncoder passwordEncoder) {
        this.pacienteRepository = pacienteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/cambiar-contrasena")
    public String mostrarFormulario(Authentication authentication) {
        if (authentication == null
                || !authentication.isAuthenticated()) {
            return "redirect:/";
        }

        return "cambiar-contrasena";
    }

    @PostMapping("/cambiar-contrasena")
    public String cambiarContrasena(
            Authentication authentication,
            @RequestParam String contrasenaActual,
            @RequestParam String nuevaContrasena,
            @RequestParam String confirmarContrasena,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (authentication == null
                || !authentication.isAuthenticated()) {
            return "redirect:/";
        }

        String correo = authentication.getName()
                .trim().toLowerCase();

        Paciente paciente = pacienteRepository
                .findByCorreo(correo)
                .orElse(null);

        if (paciente == null) {
            model.addAttribute(
                    "error",
                    "No se encontró la cuenta del paciente.");
            return "cambiar-contrasena";
        }

        if (!passwordEncoder.matches(
                contrasenaActual,
                paciente.getContrasenaHash())) {

            model.addAttribute(
                    "error",
                    "La contraseña actual es incorrecta.");
            return "cambiar-contrasena";
        }

        if (nuevaContrasena.length() < 8) {
            model.addAttribute(
                    "error",
                    "La nueva contraseña debe tener al menos 8 caracteres.");
            return "cambiar-contrasena";
        }

        if (!nuevaContrasena.equals(confirmarContrasena)) {
            model.addAttribute(
                    "error",
                    "La nueva contraseña y su confirmación no coinciden.");
            return "cambiar-contrasena";
        }

        if (passwordEncoder.matches(
                nuevaContrasena,
                paciente.getContrasenaHash())) {
            model.addAttribute(
                    "error",
                    "La nueva contraseña debe ser diferente de la actual.");
            return "cambiar-contrasena";
        }

        paciente.setContrasenaHash(
                passwordEncoder.encode(nuevaContrasena));

        pacienteRepository.save(paciente);

        redirectAttributes.addFlashAttribute(
                "mensaje",
                "Tu contraseña se cambió correctamente.");

        return "redirect:/cambiar-contrasena";
    }
}
