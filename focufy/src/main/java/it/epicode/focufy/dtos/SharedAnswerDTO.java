package it.epicode.focufy.dtos;
import it.epicode.focufy.entities.enums.SharedAnswerType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SharedAnswerDTO {

    @NotNull
    private Integer questionId;

    @NotNull
    private SharedAnswerType sharedAnswerType;

    @NotBlank
    private String answerText;

}
