package it.epicode.focufy.services;

import it.epicode.focufy.dtos.UpdateUserNameOrPasswordDTO;
import it.epicode.focufy.entities.User;
import it.epicode.focufy.exceptions.NotFoundException;
import it.epicode.focufy.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Page<User> getAllUsers(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return userRepo.findAll(pageable);
    }

    public Optional<User> getUserById(int id) {
        return userRepo.findById(id);
    }

    public User updateUserNameOrPassword(int id, UpdateUserNameOrPasswordDTO userRequestBody) {
        Optional<User> userOptional = getUserById(id);
        if (userOptional.isPresent()) {
            User userToUpdate = userOptional.get();
            if(userRequestBody.getName() != null && !userRequestBody.getName().isEmpty()) {
                userToUpdate.setName(userRequestBody.getName());
            }
            if(userRequestBody.getPassword() != null && !userRequestBody.getPassword().isEmpty()) {
                String encryptedPassword = passwordEncoder.encode(userRequestBody.getPassword());
                userToUpdate.setPassword(encryptedPassword);
            }
            return userRepo.save(userToUpdate);
        } else {
            throw new NotFoundException("User with id=" + id + " not found.");
        }
    }

    public String deleteUser(int id) {
        Optional<User> userOptional = getUserById(id);
        if (userOptional.isPresent()) {
            User userToDelete = userOptional.get();
            userRepo.delete(userToDelete);
            return "User with id=" + id + " correctly deleted.";
        } else {
            throw new NotFoundException("User with id=" + id + " not found.");
        }
    }

    public User getUserByEmail(String email) {
        Optional<User> userOptional = userRepo.findByEmail(email);
        if (userOptional.isPresent()) {
            return userOptional.get();
        } else {
            throw new NotFoundException("User with email=" + email + " not found");
        }
    }

    public User updateUserLongTermGoal(int userId, String longTermGoal) {
        Optional<User> userOptional = userRepo.findById(userId);
        if (userOptional.isPresent()) {
            User userToUpdate = userOptional.get();
            userToUpdate.setLongTermGoal(longTermGoal);
            return userRepo.save(userToUpdate);
        } else {
            throw new NotFoundException("User with id=" + userId + " not found.");
        }
    }

}
