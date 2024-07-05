package it.epicode.focufy.repositories;

import it.epicode.focufy.entities.CheckpointAnswer;
import it.epicode.focufy.entities.enums.CDAnswerType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CheckpointAnswerRepo extends JpaRepository<CheckpointAnswer, Integer> {
    Page<CheckpointAnswer> findByUserId(int userId, Pageable pageable);
    Page<CheckpointAnswer> findByCheckpointDay_Id(int checkpointDayId, Pageable pageable);
    @Query("SELECT COUNT(da) FROM CheckpointAnswer da WHERE da.user.id = :userId AND da.checkpointDay.id = :checkpointDayId")
    int countByUserIdAndCheckpointDayId(@Param("userId") int userId, @Param("checkpointDayId") int checkpointDayId);

    List<CheckpointAnswer> findByAnswerTypeAndUserId(CDAnswerType answerType, int userId);
}
