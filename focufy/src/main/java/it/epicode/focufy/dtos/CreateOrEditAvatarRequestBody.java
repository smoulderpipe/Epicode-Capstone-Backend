package it.epicode.focufy.dtos;
import it.epicode.focufy.entities.Chronotype;
import it.epicode.focufy.entities.Temper;
import lombok.Data;

@Data
public class CreateOrEditAvatarRequestBody {
    private Chronotype chronotype;

    private Temper temper;
    private String image;
    private String description;
}
