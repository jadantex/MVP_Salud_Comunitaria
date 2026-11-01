
package com.saludcomunitaria.app.repository;

import com.saludcomunitaria.app.model.HistoriaClinica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoriaClinicaRepository
        extends JpaRepository<HistoriaClinica, Long> {
}
