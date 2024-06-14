package it.epicode.focufy.dtos;
import it.epicode.focufy.entities.enums.AnswerType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateOrEditAnswerRequestBody {

    @NotNull
    private int questionId;

    private int userId;

    @NotNull
    private AnswerType answerType;

    private String answerText;

    private Integer numericAnswer;

    @NotNull
    private boolean isMultipleChoice;
}
