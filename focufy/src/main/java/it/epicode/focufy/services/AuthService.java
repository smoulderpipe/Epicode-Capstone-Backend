package it.epicode.focufy.services;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import it.epicode.focufy.dtos.CreateUserDTO;
import it.epicode.focufy.dtos.LoginUserDTO;
import it.epicode.focufy.dtos.SendNewPasswordDTO;
import it.epicode.focufy.entities.User;
import it.epicode.focufy.entities.enums.UserRole;
import it.epicode.focufy.exceptions.EmailAlreadyExistsException;
import it.epicode.focufy.exceptions.NotFoundException;
import it.epicode.focufy.exceptions.UnauthorizedException;
import it.epicode.focufy.exceptions.UnconfirmedException;
import it.epicode.focufy.repositories.UserRepo;
import it.epicode.focufy.security.JwtTool;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTool jwtTool;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private UserRepo userRepo;

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.duration}")
    private int duration;

    @Transactional
    public String authenticateUserAndCreateToken(LoginUserDTO userRequestBody){
        User user = userService.getUserByEmail(userRequestBody.getEmail());

        if (user == null) {
            throw new NotFoundException("User not found. Are you sure you typed in the right email address?");
        }

        if (!user.isConfirmation()) {
            throw new UnconfirmedException("Your account is not confirmed. Please check your email to confirm your registration.");
        }

        if(passwordEncoder.matches(userRequestBody.getPassword(), user.getPassword())) {
            return jwtTool.createToken(user);
        } else {
            throw new UnauthorizedException("An error occurred during authentication process, check your credentials and try again.");
        }
    }

    @Transactional
    public String saveUser(CreateUserDTO userRequestBody){

        if(userRepo.existsByEmail(userRequestBody.getEmail())){
            throw new EmailAlreadyExistsException("Email " + userRequestBody.getEmail() + " is already in use.");
        }
        User userToSave = new User();
        userToSave.setName(userRequestBody.getName());
        userToSave.setEmail(userRequestBody.getEmail());
        userToSave.setPassword(passwordEncoder.encode(userRequestBody.getPassword()));
        userToSave.setUserRole(UserRole.USER);
        userToSave.setConfirmation(false);
        userRepo.save(userToSave);

        String token = generateConfirmationToken(userToSave);
        sendConfirmationEmail(userToSave, token);

        return "User registered successfully. Please check your email to confirm your registration.";
    }

    public String generateConfirmationToken(User user) {
        String token = Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
                .compact();

        return token;
    }

    public void sendConfirmationEmail(User user, String token) {
        String confirmationUrl = "https://netlifydeploy--focufy.netlify.app/confirm-registration?token=" + token;
        String message = "Please click the following link to confirm your registration: " + confirmationUrl;

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(user.getEmail());
        email.setSubject("Registration Confirmation");
        email.setText(message);
        javaMailSender.send(email);
    }

    public boolean confirmUser(String token) {
        try {
            jwtTool.verifyToken(token);

            int userId = jwtTool.getIdFromToken(token);
            User user = userRepo.findById(userId).orElse(null);

            if (user == null) {
                return false;
            }

            user.setConfirmation(true);
            userRepo.save(user);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String generateRandomPassword(int length) {
        return RandomStringUtils.randomAlphanumeric(length);
    }

    public void requestNewPassword (SendNewPasswordDTO sendNewPasswordDTO) {
        Optional<User> userOptional = userRepo.findByEmail(sendNewPasswordDTO.getEmail());
        if(userOptional.isPresent()){
            User userToUpdate = userOptional.get();
            String newPassword = generateRandomPassword(10);
            String encryptedPassword = passwordEncoder.encode(newPassword);

            userToUpdate.setPassword(encryptedPassword);
            userRepo.save(userToUpdate);
            sendNewPasswordEmail(userToUpdate.getEmail(), newPassword);
        } else {
            throw new NotFoundException("User with email=" + sendNewPasswordDTO.getEmail() + " not found.");
        }
    }

    public void sendNewPasswordEmail (String email, String newPassword) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setSubject("FOCUFY APP - Your new password");
        mailMessage.setText("Your new password is: " + newPassword);

        javaMailSender.send(mailMessage);
    }

}
