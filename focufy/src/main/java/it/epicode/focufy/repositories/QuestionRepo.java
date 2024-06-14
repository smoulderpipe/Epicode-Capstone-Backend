package it.epicode.focufy.repositories;

import it.epicode.focufy.entities.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepo extends JpaRepository<Question, Integer> {
}
