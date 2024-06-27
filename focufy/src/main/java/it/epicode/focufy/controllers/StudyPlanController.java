package it.epicode.focufy.controllers;
import it.epicode.focufy.dtos.StudyPlanDTO;
import it.epicode.focufy.dtos.StudyPlanResponseDTO;
import it.epicode.focufy.entities.StudyPlan;
import it.epicode.focufy.exceptions.NotFoundException;
import it.epicode.focufy.services.StudyPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class StudyPlanController {

    @Autowired
    private StudyPlanService studyPlanService;

    @GetMapping("/api/users/{userId}/studyplans")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    public ResponseEntity<StudyPlanResponseDTO> getStudyPlanByUserId(@PathVariable int userId){
        StudyPlanResponseDTO studyPlanDTO = studyPlanService.getStudyPlanByUserId(userId);
        return new ResponseEntity<>(studyPlanDTO, HttpStatus.OK);
    }

    @PostMapping("/api/users/{userId}/studyplans")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    public ResponseEntity<StudyPlanResponseDTO> createStudyPlan(@PathVariable int userId, @RequestBody @Validated StudyPlanDTO studyPlanDTO) {
        studyPlanDTO.setUserId(userId);
        StudyPlan studyPlan = studyPlanService.saveStudyPlanAndCreateDays(studyPlanDTO);
        StudyPlanResponseDTO responseDTO = new StudyPlanResponseDTO(studyPlan);
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/api/users/{userId}/addMantras")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    public ResponseEntity<String> addMantrasToStudyPlan(@PathVariable int userId) {
        try {
            studyPlanService.addMantrasToStudyPlanByMantraType(userId);
            String jsonResponse = "{\"message\": \"Mantras added to study plan for user with id " + userId + "\"}";
            return ResponseEntity.ok().body(jsonResponse);
        } catch (NotFoundException e) {
            // Gestisci l'eccezione se l'utente non è trovato
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }

    }

    @DeleteMapping("/api/users/{userId}/studyplans")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    public ResponseEntity<String> deleteStudyPlan(@PathVariable int userId){
        studyPlanService.deleteStudyPlanByUserId(userId);
        return ResponseEntity.ok("Study plan for user with id " + userId + " correctly deleted");
    }
}
