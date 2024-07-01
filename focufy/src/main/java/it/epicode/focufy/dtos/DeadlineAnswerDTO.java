package it.epicode.focufy.dtos;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DeadlineAnswerDTO {

    @NotNull
    private int questionId;
    @NotEmpty
    private String answerText;
    @NotNull
    private int deadlineDayId;

    private int userId;
}
