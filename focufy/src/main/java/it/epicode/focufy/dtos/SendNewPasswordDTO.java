package it.epicode.focufy.dtos;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class SendNewPasswordDTO {

    @NotEmpty
    private String email;
}
