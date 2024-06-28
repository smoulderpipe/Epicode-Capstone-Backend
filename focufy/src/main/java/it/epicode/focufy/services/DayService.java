package it.epicode.focufy.services;

import it.epicode.focufy.entities.CheckpointDay;
import it.epicode.focufy.entities.Day;
import it.epicode.focufy.entities.DeadlineDay;
import it.epicode.focufy.entities.Question;
import it.epicode.focufy.repositories.CheckpointDayRepo;
import it.epicode.focufy.repositories.DeadlineDayRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DayService {

    @Autowired
    private DeadlineDayRepo deadlineDayRepo;

    @Autowired
    private CheckpointDayRepo checkpointDayRepo;

    @Transactional
    public DeadlineDay saveDeadlineDay(DeadlineDay deadlineDay){
        return deadlineDayRepo.save(deadlineDay);
    }

    @Transactional
    public CheckpointDay checkpointDay(CheckpointDay checkpointDay){
        return checkpointDayRepo.save(checkpointDay);
    }

    @Transactional
    public DeadlineDay saveDeadlineDayWithQuestions(DeadlineDay deadlineDay, List<Question> deadlineQuestions, List<Question> restartQuestions) {

        deadlineDay = deadlineDayRepo.save(deadlineDay);
        associateQuestionsWithDay(deadlineDay, deadlineQuestions, restartQuestions);

        return deadlineDay;
    }

    @Transactional
    public CheckpointDay saveCheckpointDayWithQuestions(CheckpointDay checkpointDay, List<Question> checkpointQuestions, List<Question> restartQuestions) {
        // Salva il CheckpointDay per ottenere l'ID generato
        checkpointDay = checkpointDayRepo.save(checkpointDay);

        // Assegna domande di tipo CHECKPOINT e RESTART al CheckpointDay
        associateQuestionsWithDay(checkpointDay, checkpointQuestions, restartQuestions);

        return checkpointDay;
    }

    private void associateQuestionsWithDay(Day day, List<Question> primaryQuestions, List<Question> secondaryQuestions) {
        if (day instanceof DeadlineDay) {
            DeadlineDay deadlineDay = (DeadlineDay) day;
            deadlineDay.getQuestions().addAll(primaryQuestions);
            deadlineDay.getQuestions().addAll(secondaryQuestions);
            deadlineDayRepo.save(deadlineDay); // Salvataggio aggiornato con le domande associate
        } else if (day instanceof CheckpointDay) {
            CheckpointDay checkpointDay = (CheckpointDay) day;
            checkpointDay.getQuestions().addAll(primaryQuestions);
            checkpointDay.getQuestions().addAll(secondaryQuestions);
            checkpointDayRepo.save(checkpointDay); // Salvataggio aggiornato con le domande associate
        } else {
            throw new IllegalArgumentException("Unsupported day type: " + day.getClass().getSimpleName());
        }
    }
}
