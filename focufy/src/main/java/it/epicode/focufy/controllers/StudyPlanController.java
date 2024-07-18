package it.epicode.focufy.controllers;
import it.epicode.focufy.dtos.StudyPlanDTO;
import it.epicode.focufy.dtos.StudyPlanResponseDTO;
import it.epicode.focufy.entities.StudyPlan;
import it.epicode.focufy.entities.User;
import it.epicode.focufy.exceptions.BadRequestException;
import it.epicode.focufy.exceptions.NotFoundException;
import it.epicode.focufy.exceptions.UnauthorizedException;
import it.epicode.focufy.exceptions.UserAlreadyHasStudyPlanException;
import it.epicode.focufy.services.StudyPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class StudyPlanController {

    @Autowired
    private StudyPlanService studyPlanService;

    @GetMapping("/api/users/{userId}/studyplans")
    @PreAuthorize("#userId == authentication.principal.id or hasAuthority('ADMIN')")
    public ResponseEntity<StudyPlanResponseDTO> getStudyPlanByUserId(@PathVariable int userId){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int authenticatedUserId = ((User) authentication.getPrincipal()).getId();

        if (authenticatedUserId != userId) {
            throw new UnauthorizedException("You are not allowed see study plans belonging to other users.");
        }

        StudyPlanResponseDTO studyPlanDTO = studyPlanService.getStudyPlanByUserId(userId);
        return new ResponseEntity<>(studyPlanDTO, HttpStatus.OK);
    }

    @PostMapping("/api/users/{userId}/studyplans")
    @PreAuthorize("#userId == authentication.principal.id or hasAuthority('ADMIN')")
    public ResponseEntity<StudyPlanResponseDTO> createStudyPlan(@PathVariable int userId, @RequestBody @Validated StudyPlanDTO studyPlanDTO, BindingResult bindingResult) {
        validateBindingResult(bindingResult);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int authenticatedUserId = ((User) authentication.getPrincipal()).getId();

        if (authenticatedUserId != userId) {
            throw new UnauthorizedException("You are not allowed set study plans for other users.");
        }
        studyPlanDTO.setUserId(userId);
        StudyPlan studyPlan = studyPlanService.saveStudyPlanAndCreateDays(studyPlanDTO);
        StudyPlanResponseDTO responseDTO = new StudyPlanResponseDTO(studyPlan);
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/api/users/{userId}/addMantras")
    @PreAuthorize("#userId == authentication.principal.id or hasAuthority('ADMIN')")
    public ResponseEntity<String> addMantrasToStudyPlan(@PathVariable int userId) {
        try {
            studyPlanService.addMantrasToStudyPlanByMantraType(userId);
            String jsonResponse = "{\"message\": \"Mantras added to study plan for user with id " + userId + "\"}";
            return ResponseEntity.ok().body(jsonResponse);
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @DeleteMapping("/api/users/{userId}/studyplans")
    @PreAuthorize("#userId == authentication.principal.id or hasAuthority('ADMIN')")
    public ResponseEntity<String> deleteStudyPlan(@PathVariable int userId){
        studyPlanService.deleteStudyPlanByUserId(userId);
        return ResponseEntity.ok("Study plan for user with id " + userId + " correctly deleted");
    }

    @ExceptionHandler(UserAlreadyHasStudyPlanException.class)
    public ResponseEntity<String> handleConflict(UserAlreadyHasStudyPlanException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    private void validateBindingResult(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BadRequestException(bindingResult.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .reduce("", (s1, s2) -> s1 + s2));
        }
    }
}
