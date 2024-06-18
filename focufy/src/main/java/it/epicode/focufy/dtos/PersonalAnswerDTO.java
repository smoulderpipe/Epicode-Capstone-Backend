package it.epicode.focufy.dtos;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PersonalAnswerDTO {
    @NotNull
    private Integer questionId;

    @NotBlank
    private String answerText;

    @NotNull
    private Integer userId;

    private int timeDays;

    private String shortTermGoal;

    private String longTermGoal;

    private int satisfaction;

    private boolean restart;

}
