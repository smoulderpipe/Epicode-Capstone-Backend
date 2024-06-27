package it.epicode.focufy.dtos;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Setter;

@Data
public class StudyPlanDTO {

    @NotBlank
    private String shortTermGoal;

    @Setter
    private int userId;

    @NotNull
    @Min(value = 1)
    @Max(value = 100)
    private int numberOfDays;

}
