package it.epicode.focufy.dtos;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserDTO {

    @NotEmpty(message="The name cannot be empty")
    @Size(min = 3, max = 20, message = "The name must contain between 3 and 20 characters")
    private String name;

    @NotBlank(message="The email cannot be empty")
    @Email(message="The email must be correct")
    private String email;

    @NotBlank(message="The password cannot be blank, nor can it contain only blank characters")
    @Size(min = 8, max = 30, message="The password must contain between 8 and 20 characters")
    private String password;
}
