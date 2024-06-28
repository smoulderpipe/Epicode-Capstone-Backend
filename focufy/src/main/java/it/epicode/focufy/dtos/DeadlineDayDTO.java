package it.epicode.focufy.dtos;

import it.epicode.focufy.entities.DeadlineDay;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class DeadlineDayDTO extends DayDTO {

    private List<QuestionDTO> questions;

    public DeadlineDayDTO(DeadlineDay deadlineDay) {
        super(deadlineDay.getId(), "DeadlineDay", deadlineDay.getName());
        this.questions = deadlineDay.getQuestions().stream()
                .map(QuestionDTO::createFromQuestion)
                .collect(Collectors.toList());
    }

    public DeadlineDayDTO() {
        super();
    }
}