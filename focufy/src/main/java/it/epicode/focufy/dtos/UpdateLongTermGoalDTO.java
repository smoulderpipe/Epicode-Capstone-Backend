package it.epicode.focufy.dtos;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class UpdateLongTermGoalDTO {
    @NotEmpty
    private String longTermGoal;
}
