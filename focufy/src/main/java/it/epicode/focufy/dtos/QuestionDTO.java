package it.epicode.focufy.dtos;
import it.epicode.focufy.entities.Question;
import it.epicode.focufy.entities.enums.QuestionType;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class QuestionDTO {

    private int id;

    @NotEmpty
    private String questionText;

    private QuestionType questionType;


    public QuestionDTO(int id, String questionText, QuestionType questionType) {
        this.id = id;
        this.questionText = questionText;
        this.questionType = questionType;
    }

    public QuestionDTO() {
    }

    public static QuestionDTO createFromQuestion(Question question) {
        QuestionDTO questionDTO = new QuestionDTO();
        questionDTO.setId(question.getId());
        questionDTO.setQuestionText(question.getQuestionText());
        questionDTO.setQuestionType(question.getQuestionType());
        return questionDTO;
    }

}