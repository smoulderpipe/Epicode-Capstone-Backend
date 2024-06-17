package it.epicode.focufy.dtos;

import it.epicode.focufy.entities.Chronotype;
import it.epicode.focufy.entities.Temper;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class AvatarDTO {
    @NotEmpty
    private String description;

    private String image;

    private Chronotype chronotypeType;

    private Temper temperType;
}
