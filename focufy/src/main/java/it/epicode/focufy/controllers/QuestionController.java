package it.epicode.focufy.controllers;
import it.epicode.focufy.dtos.QuestionDTO;
import it.epicode.focufy.entities.Question;
import it.epicode.focufy.exceptions.BadRequestException;
import it.epicode.focufy.exceptions.NotFoundException;
import it.epicode.focufy.services.QuestionService;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@RestController
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @GetMapping("/api/questions")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    public Page<Question> getQuestions(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int size,
                                       @RequestParam(defaultValue = "id") String sortBy){
        return questionService.getAllQuestions(page, size, sortBy);
    }

    @GetMapping("/api/questions/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    public Question getQuestion(@PathVariable int id) {
        return questionService.getQuestionById(id)
                .orElseThrow(() -> new NotFoundException("Question with id=" + id + " not found."));
    }

    @PostMapping("/api/questions")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> registerQuestion(@RequestBody @Validated QuestionDTO questionRequestBody, BindingResult bindingResult){

        if(bindingResult.hasErrors()){
            throw new BadRequestException(bindingResult.getAllErrors().stream().map(objectError -> objectError.getDefaultMessage()).reduce("", ((s, s2) -> s+s2)));
        }
        String resultMessage = questionService.saveQuestion(questionRequestBody);
        return ResponseEntity.ok(resultMessage);
    }

    @PutMapping("/api/questions/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> editQuestion(@PathVariable int id, @RequestBody QuestionDTO questionRequestBody, BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            throw new BadRequestException(bindingResult.getAllErrors().stream().map(objectError -> objectError.getDefaultMessage()).reduce("", ((s, s2) -> s+s2)));
        }
        Question updatedQuestion = questionService.updateQuestion(id, questionRequestBody);
        return ResponseEntity.ok(updatedQuestion);
    }

    @DeleteMapping("/api/questions/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> removeQuestion(@PathVariable int id) {
        String message = questionService.deleteQuestion(id);
        return ResponseEntity.ok().body(message);
    }
    
    @PostMapping("/api/questions/upload")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> uploadQuestionsFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new BadRequestException("File is empty");
        }

        try {
            File tempFile = File.createTempFile("questions", ".tmp");
            FileUtils.copyInputStreamToFile(file.getInputStream(), tempFile);

            try (BufferedReader reader = new BufferedReader(new FileReader(tempFile))) {
                questionService.loadQuestionsFromFile(reader);
                return ResponseEntity.ok("Questions uploaded successfully");
            } finally {
                FileUtils.deleteQuietly(tempFile);
            }
        } catch (IOException e) {
            throw new BadRequestException("Failed to process file: " + e.getMessage());
        }
    }



}
