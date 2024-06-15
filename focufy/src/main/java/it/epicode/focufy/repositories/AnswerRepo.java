package it.epicode.focufy.repositories;
import it.epicode.focufy.entities.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AnswerRepo extends JpaRepository<Answer, Integer> {
    List<Answer> findByUserId(int userId);
}
