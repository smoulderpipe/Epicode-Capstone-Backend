package it.epicode.focufy.dtos;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class SendNewPasswordDTO {

    @NotEmpty(message="The email cannot be empty")
    @Email(message="The email must be correct")
    private String email;
}
