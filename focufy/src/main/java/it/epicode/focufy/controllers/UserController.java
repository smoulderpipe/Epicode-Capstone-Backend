package it.epicode.focufy.controllers;
import it.epicode.focufy.dtos.CreateUserDTO;
import it.epicode.focufy.entities.Avatar;
import it.epicode.focufy.entities.User;
import it.epicode.focufy.exceptions.BadRequestException;
import it.epicode.focufy.exceptions.NotFoundException;
import it.epicode.focufy.repositories.UserRepo;
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
    @PreAuthorize("hasAuthority('ADMIN')")
    public User getUser (@PathVariable int id){
        return userService.getUserById(id).orElseThrow(() -> new NotFoundException("User with id=" + id + " not found."));
    }

    @PutMapping("/api/users/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public User editUser(@PathVariable int id, @RequestBody @Validated CreateUserDTO userRequestBody, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            throw new BadRequestException(bindingResult.getAllErrors().stream().map(objectError -> objectError.getDefaultMessage()).reduce("", ((s, s2) -> s+s2)));
        }
        return userService.updateUser(id, userRequestBody);
    }

    @DeleteMapping("/api/users/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String removeUser(@PathVariable int id){
        return userService.deleteUser(id);
    }

    @GetMapping("/api/users/{userId}/avatar")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public ResponseEntity<Avatar> getUserAvatar(@PathVariable Integer userId) {
        Optional<User> user = userRepo.findById(userId);
        if (user.isPresent()) {
            Avatar avatar = user.get().getAvatar();
            return ResponseEntity.ok(avatar);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
