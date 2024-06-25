package it.epicode.focufy.dtos;

import it.epicode.focufy.entities.enums.SharedAnswerType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignSharedAnswerDTO {

    @NotNull
    private Integer questionId;

    @NotNull
    private Integer answerId;

}
