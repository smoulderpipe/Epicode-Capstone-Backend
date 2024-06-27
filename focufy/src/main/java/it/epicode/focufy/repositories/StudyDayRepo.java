package it.epicode.focufy.repositories;

import it.epicode.focufy.entities.StudyDay;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyDayRepo extends JpaRepository<StudyDay, Integer> {
}
