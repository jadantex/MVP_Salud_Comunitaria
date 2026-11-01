
package com.saludcomunitaria.app.controller;

import com.saludcomunitaria.app.model.Paciente;
import com.saludcomunitaria.app.repository.CitaRepository;
import com.saludcomunitaria.app.repository.PacienteRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HistoriaClinicaController {

    private final CitaRepository citaRepository;
    private final PacienteRepository pacienteRepository;

    public HistoriaClinicaController(
            CitaRepository citaRepository,
            PacienteRepository pacienteRepository) {
        this.citaRepository = citaRepository;
        this.pacienteRepository = pacienteRepository;
    }

    @GetMapping("/historia-clinica")
    public String consultarHistoria(
            Authentication authentication,
            Model model) {

        Paciente paciente = pacienteRepository
                .findByCorreo(authentication.getName())
                .orElseThrow(() ->
                        new IllegalStateException(
                                "No se encontró el paciente autenticado."));

        model.addAttribute(
                "citas",
                citaRepository.findByPaciente_IdOrderByFechaDescHoraDesc(
                        paciente.getId()));

        model.addAttribute("paciente", paciente);

        return "historia-clinica";
    }
}
