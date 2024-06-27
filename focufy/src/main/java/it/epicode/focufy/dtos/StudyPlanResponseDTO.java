package it.epicode.focufy.dtos;

import it.epicode.focufy.entities.*;
import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class StudyPlanResponseDTO {
    private int id;
    private String shortTermGoal;
    private int userId;
    private List<DayDTO> days;

    public StudyPlanResponseDTO(StudyPlan studyPlan) {
        this.id = studyPlan.getId();
        this.shortTermGoal = studyPlan.getShortTermGoal();
        this.userId = studyPlan.getUser().getId();
        this.days = studyPlan.getDays().stream().map(this::mapToDayDTO).collect(Collectors.toList());
    }

    private DayDTO mapToDayDTO(Day day) {
        if (day instanceof StudyDay) {
            return mapToStudyDayDTO((StudyDay) day);
        } else if (day instanceof CheckpointDay) {
            return mapToCheckpointDayDTO((CheckpointDay) day);
        } else if (day instanceof DeadlineDay) {
            return mapToDeadlineDayDTO((DeadlineDay) day);
        }
        return null; // Gestire altri tipi di giorno se necessario
    }

    private StudyDayDTO mapToStudyDayDTO(StudyDay studyDay) {
        StudyDayDTO studyDayDTO = new StudyDayDTO();
        studyDayDTO.setId(studyDay.getId());
        studyDayDTO.setType("StudyDay");
        studyDayDTO.setMantra(studyDay.getMantra() != null ? studyDay.getMantra().getText() : null);
        studyDayDTO.setActivitySessions(studyDay.getActivitySessions().stream()
                .map(session -> new ActivitySessionDTO(session.getActivitySessionType(), session.getDuration(), session.getStartTime()))
                .collect(Collectors.toList()));
        return studyDayDTO;
    }

    private CheckpointDayDTO mapToCheckpointDayDTO(CheckpointDay checkpointDay) {
        CheckpointDayDTO checkpointDayDTO = new CheckpointDayDTO();
        checkpointDayDTO.setId(checkpointDay.getId());
        checkpointDayDTO.setType("CheckpointDay");

        List<Question> questions = checkpointDay.getQuestions();
        if (questions != null) {
            checkpointDayDTO.setQuestions(questions.stream()
                    .map(question -> new QuestionDTO(question.getId(), question.getQuestionText()))
                    .collect(Collectors.toList()));
        } else {
            checkpointDayDTO.setQuestions(Collections.emptyList()); // o gestisci il caso in un altro modo a seconda dei requisiti
        }

        return checkpointDayDTO;
    }

    private DeadlineDayDTO mapToDeadlineDayDTO(DeadlineDay deadlineDay) {
        DeadlineDayDTO deadlineDayDTO = new DeadlineDayDTO();
        deadlineDayDTO.setId(deadlineDay.getId());
        deadlineDayDTO.setType("DeadlineDay");

        List<Question> questions = deadlineDay.getQuestions();
        if(questions != null){
            deadlineDayDTO.setQuestions(questions.stream()
                    .map(question -> new QuestionDTO(question.getId(), question.getQuestionText()))
                    .collect(Collectors.toList()));
        } else {
            deadlineDayDTO.setQuestions(Collections.emptyList());
        }

        return deadlineDayDTO;
    }
}
