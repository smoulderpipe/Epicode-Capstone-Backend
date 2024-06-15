package it.epicode.focufy.dtos;
import it.epicode.focufy.entities.enums.TemperType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateOrEditTemperRequestBody {

    @NotNull
    private TemperType temperType;

    @NotEmpty
    private String description;
}
