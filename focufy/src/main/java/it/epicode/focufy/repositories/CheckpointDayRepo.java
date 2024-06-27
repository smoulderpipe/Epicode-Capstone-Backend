package it.epicode.focufy.repositories;

import it.epicode.focufy.entities.CheckpointDay;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheckpointDayRepo extends JpaRepository<CheckpointDay, Integer> {
}
