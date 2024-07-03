package it.epicode.focufy.repositories;

import it.epicode.focufy.entities.CheckpointAnswer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CheckpointAnswerRepo extends JpaRepository<CheckpointAnswer, Integer> {
    Page<CheckpointAnswer> findByUserId(int userId, Pageable pageable);
    Page<CheckpointAnswer> findByCheckpointDay_Id(int checkpointDayId, Pageable pageable);
//    boolean existsByUserIdAndCheckpointDayId(@Param("userId") int userId, @Param("checkpointDayId") int checkpointDayId);
    @Query("SELECT COUNT(da) FROM CheckpointAnswer da WHERE da.user.id = :userId AND da.checkpointDay.id = :checkpointDayId")
    int countByUserIdAndCheckpointDayId(@Param("userId") int userId, @Param("checkpointDayId") int checkpointDayId);
}
