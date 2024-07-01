package it.epicode.focufy.repositories;

import it.epicode.focufy.entities.CheckpointAnswer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheckpointAnswerRepo extends JpaRepository<CheckpointAnswer, Integer> {
    Page<CheckpointAnswer> findByUserId(int userId, Pageable pageable);
}
