package it.epicode.focufy.controllers;
import it.epicode.focufy.dtos.CreateOrEditAnswerRequestBody;
import it.epicode.focufy.dtos.CreateOrEditQuestionRequestBody;
import it.epicode.focufy.entities.Answer;
import it.epicode.focufy.entities.Question;
import it.epicode.focufy.exceptions.BadRequestException;
import it.epicode.focufy.exceptions.NotFoundException;
import it.epicode.focufy.services.AnswerService;
import it.epicode.focufy.services.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
public class AnswerController {

    @Autowired
    private AnswerService answerService;

    @GetMapping("/api/answers")
    public Page<Answer> getAnswers(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size,
                                     @RequestParam(defaultValue = "id") String sortBy){
        return answerService.getAllAnswers(page, size, sortBy);
    }

    @GetMapping("/api/answers/{id}")
    public Answer getAnswer(@PathVariable int id) {
        return answerService.getAnswerById(id)
                .orElseThrow(() -> new NotFoundException("Answer with id=" + id + " not found."));
    }

    @PostMapping("/api/answers")
    public ResponseEntity<?> registerAnswer(@RequestBody @Validated CreateOrEditAnswerRequestBody answerRequestBody, BindingResult bindingResult){

        if(bindingResult.hasErrors()){
            throw new BadRequestException(bindingResult.getAllErrors().stream().map(objectError -> objectError.getDefaultMessage()).reduce("", ((s, s2) -> s+s2)));
        }
        String resultMessage = answerService.saveAnswer(answerRequestBody);
        return ResponseEntity.ok(resultMessage);
    }

    @PutMapping("/api/answers/{id}")
    public ResponseEntity<?> editAnswer(@PathVariable int id, @RequestBody CreateOrEditAnswerRequestBody answerRequestBody, BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            throw new BadRequestException(bindingResult.getAllErrors().stream().map(objectError -> objectError.getDefaultMessage()).reduce("", ((s, s2) -> s+s2)));
        }
        Answer updatedAnswer = answerService.updateAnswer(id, answerRequestBody);
        return ResponseEntity.ok(updatedAnswer);
    }

    @DeleteMapping("/api/answers/{id}")
    public ResponseEntity<?> removeAnswer(@PathVariable int id) {
        String message = answerService.deleteAnswer(id);
        return ResponseEntity.ok().body(message);
    }
}
