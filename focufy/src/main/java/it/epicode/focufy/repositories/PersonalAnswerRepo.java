package it.epicode.focufy.repositories;
import it.epicode.focufy.entities.PersonalAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PersonalAnswerRepo extends JpaRepository<PersonalAnswer, Integer> {
    List<PersonalAnswer> findByUserId(int userId);
    long countByUser_Id(int userId);
}
