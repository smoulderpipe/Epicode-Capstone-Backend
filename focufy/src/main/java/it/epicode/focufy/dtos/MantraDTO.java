package it.epicode.focufy.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MantraDTO {
    @NotNull
    private String text;
}
