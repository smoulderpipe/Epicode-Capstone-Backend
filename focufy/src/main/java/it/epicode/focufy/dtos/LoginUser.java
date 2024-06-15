package it.epicode.focufy.dtos;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginUser {

    @NotBlank(message="The email cannot be empty")
    @Email(message="The email must be correct")
    private String email;

    @NotBlank(message="The password cannot be blank, nor can it contain only blank characters")
    @Size(min = 8, max = 30, message="The password must contain between 8 and 20 characters")
    private String password;
}
