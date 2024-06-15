package it.epicode.focufy.dtos;
import it.epicode.focufy.entities.enums.QuestionType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateOrEditQuestion {

    @NotEmpty
    private String questionText;

    @NotNull
    private QuestionType questionType;

}
