package it.epicode.focufy.repositories;

import it.epicode.focufy.entities.Chronotype;
import it.epicode.focufy.entities.enums.ChronotypeType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChronotypeRepo extends JpaRepository<Chronotype, Integer> {
    Optional<Chronotype> findByChronotypeType(ChronotypeType chronotypeType);
}
