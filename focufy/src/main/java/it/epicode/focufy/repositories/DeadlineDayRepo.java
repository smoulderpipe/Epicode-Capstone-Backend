package it.epicode.focufy.repositories;

import it.epicode.focufy.entities.DeadlineDay;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeadlineDayRepo extends JpaRepository<DeadlineDay, Integer> {
}
