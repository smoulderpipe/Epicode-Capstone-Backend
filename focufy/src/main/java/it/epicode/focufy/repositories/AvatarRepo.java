package it.epicode.focufy.repositories;
import it.epicode.focufy.entities.Avatar;
import it.epicode.focufy.entities.Chronotype;
import it.epicode.focufy.entities.Temper;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AvatarRepo extends JpaRepository<Avatar, Integer> {
    Optional<Avatar> findByChronotypeAndTemper(Chronotype chronotype, Temper temper);
}
