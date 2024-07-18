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

    @NotNull(message = "Number of days cannot be null")
    @Min(value = 1, message = "Number of days cannot be less than 1")
    @Max(value = 365, message = "Number of days cannot be greater than 365")
    private int numberOfDays;

}
