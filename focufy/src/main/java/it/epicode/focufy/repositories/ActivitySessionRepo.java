package it.epicode.focufy.repositories;

import it.epicode.focufy.entities.ActivitySession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivitySessionRepo extends JpaRepository<ActivitySession, Integer> {
}
