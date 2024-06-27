package it.epicode.focufy.dtos;

import it.epicode.focufy.entities.Day;
import it.epicode.focufy.entities.StudyPlan;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class StudyPlanResponseDTO {
    private int id;
    private String shortTermGoal;
    private int userId;
    private List<Integer> dayIds;

    public StudyPlanResponseDTO(StudyPlan studyPlan) {
        this.id = studyPlan.getId();
        this.shortTermGoal = studyPlan.getShortTermGoal();
        this.userId = studyPlan.getUser().getId();
        this.dayIds = studyPlan.getDays().stream().map(Day::getId).collect(Collectors.toList());
    }
}
