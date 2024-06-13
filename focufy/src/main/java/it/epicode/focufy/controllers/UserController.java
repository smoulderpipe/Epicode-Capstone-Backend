package it.epicode.focufy.controllers;
import it.epicode.focufy.dtos.CreateUserRequestBody;
import it.epicode.focufy.entities.User;
import it.epicode.focufy.exceptions.BadRequestException;
import it.epicode.focufy.exceptions.NotFoundException;
import it.epicode.focufy.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

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
    public User updateUser(@PathVariable int id, @RequestBody @Validated CreateUserRequestBody userRequestBody, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            throw new BadRequestException(bindingResult.getAllErrors().stream().map(objectError -> objectError.getDefaultMessage()).reduce("", ((s, s2) -> s+s2)));
        }
        return userService.updateUser(id, userRequestBody);
    }

    @DeleteMapping("/api/users/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String deleteUser(@PathVariable int id){
        return userService.deleteUser(id);
    }
}
