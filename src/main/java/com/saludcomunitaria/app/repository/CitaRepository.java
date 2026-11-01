
package com.saludcomunitaria.app.repository;

import com.saludcomunitaria.app.model.Cita;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface CitaRepository extends JpaRepository<Cita, Long> {

    // Verificar si ya existe una cita activa en ese horario
    boolean existsByProfesionalAndFechaAndHoraAndEstadoNot(
            String profesional,
            LocalDate fecha,
            LocalTime hora,
            String estado);

    // Consultar únicamente las citas de un paciente
    List<Cita> findByPaciente_IdOrderByFechaDescHoraDesc(Long pacienteId);

    // Buscar una cita solo si pertenece al paciente indicado
    Optional<Cita> findByIdAndPaciente_Id(Long id, Long pacienteId);
}
