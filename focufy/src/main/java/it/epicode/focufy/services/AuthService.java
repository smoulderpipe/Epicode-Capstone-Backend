package it.epicode.focufy.services;
import it.epicode.focufy.dtos.LoginUserRequestBody;
import it.epicode.focufy.entities.User;
import it.epicode.focufy.exceptions.UnauthorizedException;
import it.epicode.focufy.security.JwtTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTool jwtTool;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String authenticateUserAndCreateToken(LoginUserRequestBody userRequestBody){
        User user = userService.getUserByEmail(userRequestBody.getEmail());

        if(passwordEncoder.matches(userRequestBody.getPassword(), user.getPassword())) {
            return jwtTool.createToken(user);
        } else {
            throw new UnauthorizedException("Si è verificato un errore durante il processo di autenticazione, riprovare");
        }
    }
}
