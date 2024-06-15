package it.epicode.focufy.repositories;

import it.epicode.focufy.entities.Chronotype;
import it.epicode.focufy.entities.enums.ChronotypeType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChronotypeRepo extends JpaRepository<Chronotype, Integer> {
    Chronotype findByChronotypeType(ChronotypeType chronotypeType);
}
