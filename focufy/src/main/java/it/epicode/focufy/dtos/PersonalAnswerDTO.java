package it.epicode.focufy.dtos;
import it.epicode.focufy.entities.enums.PersonalAnswerType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PersonalAnswerDTO {
    @NotNull
    private Integer questionId;

    @NotEmpty
    private String answerText;

    private Integer userId;

    @NotNull
    private PersonalAnswerType personalAnswerType;

}
