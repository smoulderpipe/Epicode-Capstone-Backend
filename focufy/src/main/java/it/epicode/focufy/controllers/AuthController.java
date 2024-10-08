package it.epicode.focufy.controllers;

import it.epicode.focufy.dtos.CreateUserDTO;
import it.epicode.focufy.dtos.LoginUserDTO;
import it.epicode.focufy.dtos.SendNewPasswordDTO;
import it.epicode.focufy.exceptions.BadRequestException;
import it.epicode.focufy.exceptions.NotFoundException;
import it.epicode.focufy.services.AuthService;
import it.epicode.focufy.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @PostMapping("/auth/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response){
        response.setHeader("Authorization", "");
    }

    @PostMapping("/auth/register")
    public ResponseEntity<?> register(@RequestBody @Validated CreateUserDTO userRequestBody, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            throw new BadRequestException(bindingResult.getAllErrors().stream()
                    .map(objectError -> objectError.getDefaultMessage()).reduce("", (s, s2) -> s+s2));
        }
        String resultMessage = authService.saveUser(userRequestBody);
        Map<String, String> response = new HashMap<>();
        response.put("message", resultMessage);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/auth/confirm")
    public ResponseEntity<String> confirmRegistration(@RequestParam("token") String token) {
        boolean success = authService.confirmUser(token);

        if (success) {
            return ResponseEntity.ok("Registration confirmed successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired token.");
        }
    }

    @PostMapping("/auth/login")
    public String login(@RequestBody @Validated LoginUserDTO userRequestBody, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            throw new BadRequestException(bindingResult.getAllErrors().stream().map(error->error.getDefaultMessage())
                    .reduce("", (s, s2) -> s+s2));
        }
        return authService.authenticateUserAndCreateToken(userRequestBody);
    }

    @PostMapping("/auth/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody @Validated SendNewPasswordDTO sendNewPasswordDTO, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            throw new BadRequestException(bindingResult.getAllErrors().stream().map(error->error.getDefaultMessage())
                    .reduce("", (s, s2) -> s+s2));
        };
        try {
            authService.requestNewPassword(sendNewPasswordDTO);
            return ResponseEntity.ok("A new password has been sent to your email");
        } catch (NotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An unexpected error occured. Please try again later.");
        }
    }

}
