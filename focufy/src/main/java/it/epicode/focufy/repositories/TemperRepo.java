package it.epicode.focufy.repositories;
import it.epicode.focufy.entities.Temper;
import it.epicode.focufy.entities.enums.TemperType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TemperRepo extends JpaRepository<Temper, Integer> {
    Optional<Temper> findByTemperType(TemperType temperType);
}
