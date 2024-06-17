package it.epicode.focufy.dtos;
import it.epicode.focufy.entities.enums.QuestionType;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class QuestionDTO {

    @NotEmpty
    private String questionText;

    private QuestionType questionType;

}
