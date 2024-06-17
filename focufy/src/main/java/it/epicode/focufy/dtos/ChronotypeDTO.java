package it.epicode.focufy.dtos;
import it.epicode.focufy.entities.enums.ChronotypeType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChronotypeDTO {
    @NotNull
    private ChronotypeType chronotypeType;
    @NotEmpty
    private String description;
}
