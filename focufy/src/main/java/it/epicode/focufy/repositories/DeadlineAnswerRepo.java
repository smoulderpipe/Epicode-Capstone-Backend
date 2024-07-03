package it.epicode.focufy.repositories;
import it.epicode.focufy.entities.DeadlineAnswer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DeadlineAnswerRepo extends JpaRepository<DeadlineAnswer, Integer> {
    Page<DeadlineAnswer> findByUserId(int userId, Pageable pageable);
    Page<DeadlineAnswer> findByDeadlineDay_Id(int deadlineDayId, Pageable pageable);
    @Query("SELECT COUNT(da) FROM DeadlineAnswer da WHERE da.user.id = :userId AND da.deadlineDay.id = :deadlineDayId")
    int countByUserIdAndDeadlineDayId(@Param("userId") int userId, @Param("deadlineDayId") int deadlineDayId);
}
