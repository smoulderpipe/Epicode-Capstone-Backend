package it.epicode.focufy.controllers;
import it.epicode.focufy.dtos.CreateOrEditQuestion;
import it.epicode.focufy.entities.Question;
import it.epicode.focufy.exceptions.BadRequestException;
import it.epicode.focufy.exceptions.NotFoundException;
import it.epicode.focufy.services.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @GetMapping("/api/questions")
    public Page<Question> getQuestions(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int size,
                                       @RequestParam(defaultValue = "id") String sortBy){
        return questionService.getAllQuestions(page, size, sortBy);
    }

    @GetMapping("/api/questions/{id}")
    public Question getQuestion(@PathVariable int id) {
        return questionService.getQuestionById(id)
                .orElseThrow(() -> new NotFoundException("Question with id=" + id + " not found."));
    }

    @PostMapping("/api/questions")
    public ResponseEntity<?> registerQuestion(@RequestBody @Validated CreateOrEditQuestion questionRequestBody, BindingResult bindingResult){

        if(bindingResult.hasErrors()){
            throw new BadRequestException(bindingResult.getAllErrors().stream().map(objectError -> objectError.getDefaultMessage()).reduce("", ((s, s2) -> s+s2)));
        }
        String resultMessage = questionService.saveQuestion(questionRequestBody);
        return ResponseEntity.ok(resultMessage);
    }

    @PutMapping("/api/questions/{id}")
    public ResponseEntity<?> editQuestion(@PathVariable int id, @RequestBody CreateOrEditQuestion questionRequestBody, BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            throw new BadRequestException(bindingResult.getAllErrors().stream().map(objectError -> objectError.getDefaultMessage()).reduce("", ((s, s2) -> s+s2)));
        }
        Question updatedQuestion = questionService.updateQuestion(id, questionRequestBody);
        return ResponseEntity.ok(updatedQuestion);
    }

    @DeleteMapping("/api/questions/{id}")
    public ResponseEntity<?> removeQuestion(@PathVariable int id) {
        String message = questionService.deleteQuestion(id);
        return ResponseEntity.ok().body(message);
    }
}
