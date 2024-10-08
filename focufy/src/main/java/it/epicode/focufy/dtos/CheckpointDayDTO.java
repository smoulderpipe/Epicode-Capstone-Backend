package it.epicode.focufy.dtos;

import it.epicode.focufy.entities.CheckpointDay;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class CheckpointDayDTO extends DayDTO {

    private List<QuestionDTO> questions;

    private LocalDate date;

    public CheckpointDayDTO(CheckpointDay checkpointDay) {
        super(checkpointDay.getId(), "CheckpointDay", checkpointDay.getName());
        this.questions = checkpointDay.getQuestions().stream()
                .map(QuestionDTO::createFromQuestion)
                .collect(Collectors.toList());
    }

    public CheckpointDayDTO() {
        super();
    }
}