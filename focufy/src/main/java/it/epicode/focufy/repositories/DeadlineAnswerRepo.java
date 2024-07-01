package it.epicode.focufy.repositories;
import it.epicode.focufy.entities.DeadlineAnswer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeadlineAnswerRepo extends JpaRepository<DeadlineAnswer, Integer> {
    Page<DeadlineAnswer> findByUserId(int userId, Pageable pageable);

}
