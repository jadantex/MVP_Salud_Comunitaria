
package com.saludcomunitaria.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PacienteController {

    @GetMapping("/paciente")
    public String menuPaciente() {
        return "menu-paciente";
    }

    @GetMapping("/prevencion")
    public String mostrarPrevencion() {
        return "prevencion";
    }
}
