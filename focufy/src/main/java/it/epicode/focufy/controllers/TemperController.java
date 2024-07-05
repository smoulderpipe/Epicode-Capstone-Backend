package it.epicode.focufy.controllers;
import it.epicode.focufy.dtos.TemperDTO;
import it.epicode.focufy.entities.Temper;
import it.epicode.focufy.exceptions.BadRequestException;
import it.epicode.focufy.exceptions.NotFoundException;
import it.epicode.focufy.services.TemperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TemperController {

    @Autowired
    private TemperService temperService;

    @GetMapping("/api/tempers")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    public List<Temper> getTempers(){
        return temperService.getAllTempers();
    }

    @GetMapping("/api/tempers/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    public Temper getTemperById(@PathVariable int id){
        return temperService.getTemperById(id).orElseThrow(()-> new NotFoundException("Temper with id=" + id + " not found."));
    }

    @PostMapping("/api/tempers")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> registerTemper(@RequestBody @Validated TemperDTO temperRequestBody, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            throw new BadRequestException(bindingResult.getAllErrors().stream().map(objectError -> objectError.getDefaultMessage()).reduce("", ((s, s2) -> s+s2)));
        }
        String resultMessage = temperService.saveTemper(temperRequestBody);
        return ResponseEntity.ok(resultMessage);
    }

    @PutMapping("/api/tempers/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> editTemper(@PathVariable int id, @RequestBody @Validated TemperDTO temperRequestBody, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            throw new BadRequestException(bindingResult.getAllErrors().stream().map(objectError -> objectError.getDefaultMessage()).reduce("", ((s, s2) -> s+s2)));
        }
        Temper updatedTemper = temperService.updateTemper(id, temperRequestBody);
        return ResponseEntity.ok(updatedTemper);
    }

    @DeleteMapping("/api/tempers/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> removeTemper(@PathVariable int id){
        String message = temperService.deleteTemper(id);
        return ResponseEntity.ok().body(message);
    }
}
