package it.epicode.focufy.repositories;
import it.epicode.focufy.entities.SharedAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SharedAnswerRepo extends JpaRepository<SharedAnswer, Integer> {
    long countByUsers_Id(int userId);
    List<SharedAnswer> findByUsers_Id(int userId);
}
