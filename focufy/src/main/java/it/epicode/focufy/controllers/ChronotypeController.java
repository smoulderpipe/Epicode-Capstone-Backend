package it.epicode.focufy.controllers;
import it.epicode.focufy.dtos.ChronotypeDTO;
import it.epicode.focufy.entities.Chronotype;
import it.epicode.focufy.exceptions.BadRequestException;
import it.epicode.focufy.exceptions.NotFoundException;
import it.epicode.focufy.services.ChronotypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ChronotypeController {

    @Autowired
    private ChronotypeService chronotypeService;

    @GetMapping("/api/chronotypes")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    public List<Chronotype> getChronotypes(){
        return chronotypeService.getAllChronotypes();
    }

    @GetMapping("/api/chronotypes/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    public Chronotype getChronotype(@PathVariable int id){
        return chronotypeService.getChronotypeById(id).orElseThrow(()-> new NotFoundException("Chronotype with id=" + id + " not found."));
    }

    @PostMapping("/api/chronotypes")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> registerChronotype(@RequestBody @Validated ChronotypeDTO chronotypeRequestBody, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            throw new BadRequestException(bindingResult.getAllErrors().stream().map(objectError -> objectError.getDefaultMessage()).reduce("", ((s, s2) -> s+s2)));
        }
        String resultMessage = chronotypeService.saveChronotype(chronotypeRequestBody);
        return ResponseEntity.ok(resultMessage);
    }

    @PutMapping("/api/chronotypes/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> editChronotype(@PathVariable int id, @RequestBody @Validated ChronotypeDTO chronotypeRequestBody, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            throw new BadRequestException(bindingResult.getAllErrors().stream().map(objectError -> objectError.getDefaultMessage()).reduce("", ((s, s2) -> s+s2)));
        }
        Chronotype updatedChronotype = chronotypeService.updateChronotype(id, chronotypeRequestBody);
        return ResponseEntity.ok(updatedChronotype);
    }

    @DeleteMapping("/api/chronotypes/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> removeChronotype(@PathVariable int id){
        String message = chronotypeService.deleteChronotype(id);
        return ResponseEntity.ok().body(message);
    }

}
