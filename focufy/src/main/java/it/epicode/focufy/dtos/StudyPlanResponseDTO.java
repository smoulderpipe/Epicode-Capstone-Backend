package it.epicode.focufy.dtos;

import it.epicode.focufy.entities.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

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
        this.days = new ArrayList<>();
    }

    public void addDay(DayDTO dayDTO) {
        this.days.add(dayDTO);
    }
}