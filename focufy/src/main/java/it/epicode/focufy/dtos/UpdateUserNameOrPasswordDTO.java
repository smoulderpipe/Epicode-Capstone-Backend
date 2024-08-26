package it.epicode.focufy.dtos;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserNameOrPasswordDTO {

    @Size(min = 3, max = 20, message = "The name must contain between 3 and 20 characters")
    private String name;

    @Size(min = 8, max = 30, message="The password must contain between 8 and 20 characters")
    private String password;
}
