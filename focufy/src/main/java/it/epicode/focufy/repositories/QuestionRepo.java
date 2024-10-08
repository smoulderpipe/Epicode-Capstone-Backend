package it.epicode.focufy.repositories;
import it.epicode.focufy.entities.Question;
import it.epicode.focufy.entities.enums.QuestionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepo extends JpaRepository<Question, Integer> {
    long countByQuestionType(QuestionType questionType);
    List<Question> findByQuestionType(QuestionType questionType);
}
