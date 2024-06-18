package it.epicode.focufy.controllers;
import it.epicode.focufy.dtos.PersonalAnswerDTO;
import it.epicode.focufy.dtos.SharedAnswerDTO;
import it.epicode.focufy.entities.*;
import it.epicode.focufy.exceptions.BadRequestException;
import it.epicode.focufy.exceptions.NotFoundException;
import it.epicode.focufy.repositories.SharedAnswerRepo;
import it.epicode.focufy.services.AnswerService;
import it.epicode.focufy.services.AvatarService;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/answers")
public class AnswerController {

    @Autowired
    private AnswerService answerService;

    @Autowired
    private AvatarService avatarService;

    @Autowired
    private SharedAnswerRepo sharedAnswerRepo;

    @GetMapping("/shared")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public ResponseEntity<Page<SharedAnswer>> getSharedAnswers(@RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "10") int size,
                                                               @RequestParam(defaultValue = "id") String sortBy) {
        Page<SharedAnswer> sharedAnswers = answerService.getAllSharedAnswers(page, size, sortBy);
        return ResponseEntity.ok(sharedAnswers);
    }

    @GetMapping("/personal")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Page<PersonalAnswer>> getPersonalAnswers(@RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "10") int size,
                                                                   @RequestParam(defaultValue = "id") String sortBy) {
        Page<PersonalAnswer> personalAnswers = answerService.getAllPersonalAnswers(page, size, sortBy);
        return ResponseEntity.ok(personalAnswers);
    }

    @GetMapping("/shared/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public ResponseEntity<SharedAnswer> getSharedAnswerById(@PathVariable int id) {
        SharedAnswer sharedAnswer = answerService.getSharedAnswerById(id)
                .orElseThrow(() -> new NotFoundException("Shared answer with id=" + id + " not found."));
        return ResponseEntity.ok(sharedAnswer);
    }

    @GetMapping("/personal/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public ResponseEntity<PersonalAnswer> getPersonalAnswerById(@PathVariable int id) {
        PersonalAnswer personalAnswer = answerService.getPersonalAnswerById(id)
                .orElseThrow(() -> new NotFoundException("Personal answer with id=" + id + " not found."));
        return ResponseEntity.ok(personalAnswer);
    }

    @PostMapping("/shared")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> saveSharedAnswer(@RequestBody @Validated SharedAnswerDTO answerRequestBody,
                                                   BindingResult bindingResult) {
        validateBindingResult(bindingResult);
        String resultMessage = answerService.saveSharedAnswer(answerRequestBody);
        return ResponseEntity.ok(resultMessage);
    }

    @PutMapping("/shared/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<SharedAnswer> updateSharedAnswer(@PathVariable int id,
                                                           @RequestBody @Validated SharedAnswerDTO answerRequestBody,
                                                           BindingResult bindingResult) {
        validateBindingResult(bindingResult);
        SharedAnswer updatedAnswer = answerService.updateSharedAnswer(id, answerRequestBody);
        return ResponseEntity.ok(updatedAnswer);
    }

    @PutMapping("/personal/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<PersonalAnswer> updatePersonalAnswer(@PathVariable int id,
                                                               @RequestBody @Validated PersonalAnswerDTO answerRequestBody,
                                                               BindingResult bindingResult) {
        validateBindingResult(bindingResult);
        PersonalAnswer updatedAnswer = answerService.updatePersonalAnswer(id, answerRequestBody);
        return ResponseEntity.ok(updatedAnswer);
    }

    @DeleteMapping("/shared/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> deleteSharedAnswer(@PathVariable int id) {
        String message = answerService.deleteSharedAnswer(id);
        return ResponseEntity.ok(message);
    }

    @DeleteMapping("/personal/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> deletePersonalAnswer(@PathVariable int id) {
        String message = answerService.deletePersonalAnswer(id);
        return ResponseEntity.ok(message);
    }

    @PostMapping("/uploadSharedAnswers")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> uploadSharedAnswersFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new BadRequestException("File is empty");
        }

        try {
            File tempFile = File.createTempFile("sharedAnswers", ".tmp");
            FileUtils.copyInputStreamToFile(file.getInputStream(), tempFile);

            try (BufferedReader reader = new BufferedReader(new FileReader(tempFile))) {
                answerService.loadSharedAnswersFromFile(reader);
                return ResponseEntity.ok("Shared Answers uploaded successfully");
            } finally {
                FileUtils.deleteQuietly(tempFile);
            }
        } catch (IOException e) {
            throw new BadRequestException("Failed to process file: " + e.getMessage());
        }
    }

    @PutMapping("/shared/{sharedAnswerId}/assign/{userId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public ResponseEntity<?> assignSharedAnswerToUser(@PathVariable int sharedAnswerId, @PathVariable int userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int authenticatedUserId = ((User) authentication.getPrincipal()).getId();

        if (authenticatedUserId != userId) {
            throw new BadRequestException("You are not allowed to assign the Shared Answer to another user.");
        }

        SharedAnswer sharedAnswer = sharedAnswerRepo.findById(sharedAnswerId)
                .orElseThrow(() -> new NotFoundException("SharedAnswer with id=" + sharedAnswerId + " not found"));
        answerService.assignSharedAnswerToUser(sharedAnswerId, userId);
        return ResponseEntity.ok("User with id=" + userId + " assigned to Shared Answer with id=" + sharedAnswerId + " successfully");
    }

    @GetMapping("/users/{userId}/shared")
    @PreAuthorize("#userId == authentication.principal.id or hasAuthority('ADMIN')")
    public ResponseEntity<Page<SharedAnswer>> getUserSharedAnswers(@PathVariable int userId,
                                                                   @RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "10") int size,
                                                                   @RequestParam(defaultValue = "id") String sortBy) {
        Page<SharedAnswer> sharedAnswers = answerService.getOwnSharedAnswers(userId, page, size, sortBy);
        return ResponseEntity.ok(sharedAnswers);
    }

    @GetMapping("/users/{userId}/personal")
    @PreAuthorize("#userId == authentication.principal.id or hasAuthority('ADMIN')")
    public ResponseEntity<Page<PersonalAnswer>> getUserPersonalAnswers(@PathVariable int userId,
                                                                       @RequestParam(defaultValue = "0") int page,
                                                                       @RequestParam(defaultValue = "10") int size,
                                                                       @RequestParam(defaultValue = "id") String sortBy) {
        Page<PersonalAnswer> personalAnswers = answerService.getOwnPersonalAnswers(userId, page, size, sortBy);
        return ResponseEntity.ok(personalAnswers);
    }

    @PostMapping("/personal")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public ResponseEntity<String> savePersonalAnswer(@RequestBody @Validated PersonalAnswerDTO answerRequestBody,
                                                     BindingResult bindingResult) {
        validateBindingResult(bindingResult);
        String resultMessage = answerService.savePersonalAnswer(answerRequestBody);
        return ResponseEntity.ok(resultMessage);
    }

    @DeleteMapping("/user/shared/{userId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public ResponseEntity<String> clearUserSharedAnswers(@PathVariable int userId) {
        answerService.clearUserSharedAnswers(userId);
        return ResponseEntity.ok("User's shared answers cleared successfully");
    }

    private void validateBindingResult(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BadRequestException(bindingResult.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .reduce("", (s1, s2) -> s1 + s2));
        }
    }


}