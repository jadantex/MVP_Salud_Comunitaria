
package com.saludcomunitaria.app.controller;

import com.saludcomunitaria.app.model.Cita;
import com.saludcomunitaria.app.model.Paciente;
import com.saludcomunitaria.app.repository.CitaRepository;
import com.saludcomunitaria.app.repository.PacienteRepository;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CitaController {

    private final CitaRepository citaRepository;
    private final PacienteRepository pacienteRepository;

    public CitaController(
            CitaRepository citaRepository,
            PacienteRepository pacienteRepository) {

        this.citaRepository = citaRepository;
        this.pacienteRepository = pacienteRepository;
    }

    private Paciente obtenerPacienteAutenticado(
            Authentication authentication) {

        if (authentication == null
                || !authentication.isAuthenticated()) {
            throw new IllegalStateException(
                    "No hay un paciente autenticado.");
        }

        return pacienteRepository.findByCorreo(authentication.getName())
                .orElseThrow(() -> new IllegalStateException(
                        "No se encontró el paciente autenticado."));
    }

    @GetMapping("/citas/nueva")
    public String nuevaCita() {
        return "agendar-cita";
    }

    @GetMapping("/citas/profesional")
    public String seleccionarProfesional(
            @RequestParam("especialidad") String especialidad,
            Model model) {

        model.addAttribute("especialidad", especialidad);
        return "seleccionar-profesional";
    }

    @GetMapping("/citas/fecha-hora")
    public String seleccionarFechaHora(
            @RequestParam("especialidad") String especialidad,
            @RequestParam("profesional") String profesional,
            Model model) {

        model.addAttribute("especialidad", especialidad);
        model.addAttribute("profesional", profesional);
        return "fecha-hora";
    }

    @GetMapping("/citas/confirmar")
    public String confirmarCita(
            @RequestParam("especialidad") String especialidad,
            @RequestParam("profesional") String profesional,
            @RequestParam("fecha") String fecha,
            @RequestParam("hora") String hora,
            Model model) {

        model.addAttribute("especialidad", especialidad);
        model.addAttribute("profesional", profesional);
        model.addAttribute("fecha", fecha);
        model.addAttribute("hora", hora);

        return "confirmar-cita";
    }

    @PostMapping("/citas/guardar")
    public String guardarCita(
            @RequestParam("especialidad") String especialidad,
            @RequestParam("profesional") String profesional,
            @RequestParam("fecha") String fecha,
            @RequestParam("hora") String hora,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            LocalDate fechaCita = LocalDate.parse(fecha);
            LocalTime horaCita = LocalTime.parse(hora);

            if (especialidad.isBlank()
                    || profesional.isBlank()
                    || fechaCita.isBefore(LocalDate.now())) {

                redirectAttributes.addFlashAttribute(
                        "error",
                        "Los datos de la cita no son válidos.");

                return "redirect:/citas/nueva";
            }

            boolean horarioOcupado =
                    citaRepository.existsByProfesionalAndFechaAndHoraAndEstadoNot(
                            profesional,
                            fechaCita,
                            horaCita,
                            "CANCELADA");

            if (horarioOcupado) {
                redirectAttributes.addFlashAttribute(
                        "error",
                        "Este profesional ya tiene una cita reservada "
                        + "para esa fecha y hora. Por favor, selecciona "
                        + "otro horario.");

                return "redirect:/citas/nueva";
            }

            Paciente paciente =
                    obtenerPacienteAutenticado(authentication);

            Cita cita = new Cita();
            cita.setEspecialidad(especialidad);
            cita.setProfesional(profesional);
            cita.setFecha(fechaCita);
            cita.setHora(horaCita);
            cita.setEstado("PENDIENTE");
            cita.setPaciente(paciente);

            Cita guardada = citaRepository.save(cita);

            redirectAttributes.addFlashAttribute(
                    "mensaje",
                    "La cita se registró correctamente.");

            redirectAttributes.addFlashAttribute(
                    "citaId",
                    guardada.getId());

            return "redirect:/citas/resultado";

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "No fue posible registrar la cita. Verifica los datos.");

            return "redirect:/citas/nueva";
        }
    }

    @GetMapping("/citas/resultado")
    public String resultadoCita() {
        return "resultado-cita";
    }

    @GetMapping("/citas")
    public String listarCitas(
            Authentication authentication,
            Model model) {

        Paciente paciente =
                obtenerPacienteAutenticado(authentication);

        model.addAttribute(
                "citas",
                citaRepository
                        .findByPaciente_IdOrderByFechaDescHoraDesc(
                                paciente.getId()));

        return "mis-citas";
    }

    @PostMapping("/citas/cancelar")
    public String cancelarCita(
            @RequestParam("id") Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        Paciente paciente =
                obtenerPacienteAutenticado(authentication);

        return citaRepository
                .findByIdAndPaciente_Id(id, paciente.getId())
                .map(cita -> {

                    if (!"PENDIENTE".equals(cita.getEstado())) {
                        redirectAttributes.addFlashAttribute(
                                "error",
                                "Solo se pueden cancelar citas pendientes.");
                    } else {
                        cita.setEstado("CANCELADA");
                        citaRepository.save(cita);

                        redirectAttributes.addFlashAttribute(
                                "mensaje",
                                "La cita se canceló correctamente.");
                    }

                    return "redirect:/citas";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute(
                            "error",
                            "No se encontró una cita tuya con ese identificador.");

                    return "redirect:/citas";
                });
    }

    @PostMapping("/citas/atender")
    public String atenderCita(
            @RequestParam("id") Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        Paciente paciente =
                obtenerPacienteAutenticado(authentication);

        return citaRepository
                .findByIdAndPaciente_Id(id, paciente.getId())
                .map(cita -> {

                    if (!"PENDIENTE".equals(cita.getEstado())) {
                        redirectAttributes.addFlashAttribute(
                                "error",
                                "Solo se pueden atender citas pendientes.");
                    } else {
                        cita.setEstado("ATENDIDA");
                        citaRepository.save(cita);

                        redirectAttributes.addFlashAttribute(
                                "mensaje",
                                "La cita se marcó como atendida.");
                    }

                    return "redirect:/citas";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute(
                            "error",
                            "No se encontró una cita tuya con ese identificador.");

                    return "redirect:/citas";
                });
    }
}
