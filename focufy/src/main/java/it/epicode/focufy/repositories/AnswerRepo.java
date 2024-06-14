package it.epicode.focufy.repositories;
import it.epicode.focufy.entities.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepo extends JpaRepository<Answer, Integer> {
}
