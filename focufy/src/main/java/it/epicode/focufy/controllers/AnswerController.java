package it.epicode.focufy.controllers;
import it.epicode.focufy.dtos.*;
import it.epicode.focufy.entities.*;
import it.epicode.focufy.exceptions.BadRequestException;
import it.epicode.focufy.exceptions.NotFoundException;
import it.epicode.focufy.exceptions.UnauthorizedException;
import it.epicode.focufy.repositories.SharedAnswerRepo;
import it.epicode.focufy.services.AnswerService;
import it.epicode.focufy.services.AvatarService;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @GetMapping("/question/{questionId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public ResponseEntity<List<SharedAnswer>> getAnswersForQuestion(@PathVariable int questionId) {
        List<SharedAnswer> answers = answerService.getSharedAnswersForQuestion(questionId);
        return ResponseEntity.ok(answers);
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

    @PutMapping("/shared/assign/{userId}")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    public ResponseEntity<?> assignSharedAnswersToUser(@PathVariable int userId, @RequestBody List<AssignSharedAnswerDTO> assignSharedAnswers){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int authenticatedUserId = ((User) authentication.getPrincipal()).getId();

        if (authenticatedUserId != userId) {
            throw new BadRequestException("You are not allowed to assign the Shared Answers to another user.");
        }
        for (AssignSharedAnswerDTO assignSharedAnswerDTO : assignSharedAnswers) {
            SharedAnswer sharedAnswer = sharedAnswerRepo.findById(assignSharedAnswerDTO.getAnswerId())
                    .orElseThrow(() -> new NotFoundException("SharedAnswer with id=" + assignSharedAnswerDTO.getAnswerId() + " not found."));
            answerService.assignSharedAnswerToUser(sharedAnswer.getId(), userId);
        }
        Map<String, String> response = new HashMap<>();
        response.put("message", "User with id=" + userId + " assigned to Shared Answers successfully");

        return ResponseEntity.ok(response);
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

    @PostMapping("/users/{userId}/personal")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public ResponseEntity<Map<String, String>> savePersonalAnswers(@PathVariable int userId, @RequestBody List<PersonalAnswerDTO> personalAnswers) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int authenticatedUserId = ((User) authentication.getPrincipal()).getId();

        if (authenticatedUserId != userId) {
            throw new UnauthorizedException("You are not allowed to save personal answers for another user.");
        }

        Map<String, String> response = new HashMap<>();
        try {
            answerService.savePersonalAnswers(personalAnswers);
            response.put("message", "Personal answers saved successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Handle any exceptions that might occur during saving
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/user/shared/{userId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public ResponseEntity<String> clearUserSharedAnswers(@PathVariable int userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int authenticatedUserId = ((User) authentication.getPrincipal()).getId();
        if (authenticatedUserId != userId) {
            throw new BadRequestException("You are not allowed to clear another user's personal answers.");
        }
        answerService.clearUserSharedAnswers(userId);
        return ResponseEntity.ok("User's shared answers cleared successfully");
    }

    @DeleteMapping("/users/{userId}/personal")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public ResponseEntity<String> clearUserPersonalAnswers(@PathVariable int userId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int authenticatedUserId = ((User) authentication.getPrincipal()).getId();
        if (authenticatedUserId != userId) {
            throw new BadRequestException("You are not allowed to clear another user's personal answers.");
        }
        answerService.clearUserPersonalAnswers(userId);
        return ResponseEntity.ok("User's personal answers cleared successfully");
    }

    private void validateBindingResult(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BadRequestException(bindingResult.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .reduce("", (s1, s2) -> s1 + s2));
        }
    }

    @PostMapping("/users/{userId}/checkpoint")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public ResponseEntity<String> saveCheckpointAnswers(@RequestBody List<CheckpointAnswerDTO> checkpointAnswerDTOs) {
        try {
            String resultMessage = answerService.saveCheckpointAnswers(checkpointAnswerDTOs);
            return ResponseEntity.ok(resultMessage);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/users/{userId}/deadline")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public ResponseEntity<String> saveDeadlineAnswers(@RequestBody List<DeadlineAnswerDTO> deadlineAnswerDTOs) {
        try {
            String resultMessage = answerService.saveDeadlineAnswers(deadlineAnswerDTOs);
            return ResponseEntity.ok(resultMessage);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
//    @PostMapping("/checkpoint/{checkpointDayId}/answers")
//    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
//    public ResponseEntity<String> saveCheckpointAnswers(@PathVariable int checkpointDayId,
//                                                        @RequestBody List<CheckpointAnswer> answers) {
//
//        try {
//            answerService.saveCheckpointAnswers(checkpointDayId, answers);
//            return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body("Checkpoint answers saved successfully");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Failed to save checkpoint answers: " + e.getMessage());
//        }
//    }

//    @PostMapping("/deadline/{deadlineDayId}/answers")
//    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
//    public ResponseEntity<String> saveDeadlineAnswers(@PathVariable int deadlineDayId,
//                                                      @RequestBody List<DeadlineAnswer> answers) {
//        try {
//            answerService.saveDeadlineAnswers(deadlineDayId, answers);
//            return ResponseEntity.ok("Deadline answers saved successfully");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Failed to save deadline answers: " + e.getMessage());
//        }
//    }

}