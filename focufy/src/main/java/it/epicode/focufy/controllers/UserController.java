package it.epicode.focufy.controllers;
import it.epicode.focufy.dtos.UpdateLongTermGoalDTO;
import it.epicode.focufy.dtos.UpdateUserNameOrPasswordDTO;
import it.epicode.focufy.entities.Avatar;
import it.epicode.focufy.entities.CheckpointAnswer;
import it.epicode.focufy.entities.DeadlineAnswer;
import it.epicode.focufy.entities.User;
import it.epicode.focufy.exceptions.BadRequestException;
import it.epicode.focufy.exceptions.NotFoundException;
import it.epicode.focufy.repositories.UserRepo;
import it.epicode.focufy.services.AnswerService;
import it.epicode.focufy.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class UserController {

    @Autowired
    private AnswerService answerService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private UserService userService;

    @GetMapping("/api/users")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Page<User> getUsers(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size,
                                @RequestParam(defaultValue = "id") String sortBy){
        return userService.getAllUsers(page, size, sortBy);
    }

    @GetMapping("/api/users/{id}")
    @PreAuthorize("#id == authentication.principal.id or hasAuthority('ADMIN')")
    public User getUser (@PathVariable int id){
        return userService.getUserById(id).orElseThrow(() -> new NotFoundException("User with id=" + id + " not found."));
    }

    @PutMapping("/api/users/{id}")
    @PreAuthorize("#id == authentication.principal.id or hasAuthority('ADMIN')")
    public User editUser(@PathVariable int id, @RequestBody @Validated UpdateUserNameOrPasswordDTO userRequestBody, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            throw new BadRequestException(bindingResult.getAllErrors().stream().map(objectError -> objectError.getDefaultMessage()).reduce("", ((s, s2) -> s+s2)));
        }
        return userService.updateUserNameOrPassword(id, userRequestBody);
    }

    @DeleteMapping("/api/users/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String removeUser(@PathVariable int id){
        return userService.deleteUser(id);
    }

    @GetMapping("/api/users/{userId}/avatar")
    @PreAuthorize("#userId == authentication.principal.id or hasAuthority('ADMIN')")
    public ResponseEntity<Avatar> getUserAvatar(@PathVariable Integer userId) {
        Optional<User> user = userRepo.findById(userId);
        if (user.isPresent()) {
            Avatar avatar = user.get().getAvatar();
            return ResponseEntity.ok(avatar);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/api/users/{id}/long-term-goal")
    @PreAuthorize("#id == authentication.principal.id or hasAuthority('ADMIN')")
    public User updateUserLongTermGoal(@PathVariable int id, @RequestBody @Validated UpdateLongTermGoalDTO longTermGoalDTO, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            throw new BadRequestException(bindingResult.getAllErrors().stream().map(objectError -> objectError.getDefaultMessage()).reduce("", ((s, s2) -> s + s2)));
        }
        return userService.updateUserLongTermGoal(id, longTermGoalDTO.getLongTermGoal());
    }

    @GetMapping("/api/users/{userId}/checkpoint")
    @PreAuthorize("#userId == authentication.principal.id or hasAuthority('ADMIN')")
    public ResponseEntity<Page<CheckpointAnswer>> getUserCheckpointAnswers(@PathVariable int userId,
                                                                           @RequestParam(defaultValue = "0") int page,
                                                                           @RequestParam(defaultValue = "10") int size,
                                                                           @RequestParam(defaultValue = "id") String sortBy) {
        Page<CheckpointAnswer> checkpointAnswers = answerService.getOwnCheckpointAnswers(userId, page, size, sortBy);
        return ResponseEntity.ok(checkpointAnswers);
    }

    @GetMapping("/api/users/{userId}/deadline")
    @PreAuthorize("#userId == authentication.principal.id or hasAuthority('ADMIN')")
    public ResponseEntity<Page<DeadlineAnswer>> getUserDeadlineAnswers(@PathVariable int userId,
                                                                         @RequestParam(defaultValue = "0") int page,
                                                                         @RequestParam(defaultValue = "10") int size,
                                                                         @RequestParam(defaultValue = "id") String sortBy) {
        Page<DeadlineAnswer> deadlineAnswers = answerService.getOwnDeadlineAnswers(userId, page, size, sortBy);
        return ResponseEntity.ok(deadlineAnswers);
    }
}
