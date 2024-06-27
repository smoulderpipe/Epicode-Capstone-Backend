package it.epicode.focufy.repositories;

import it.epicode.focufy.entities.StudyPlan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyPlanRepo extends JpaRepository<StudyPlan, Integer> {
}
