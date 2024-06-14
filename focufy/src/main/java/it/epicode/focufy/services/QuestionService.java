package it.epicode.focufy.services;
import it.epicode.focufy.dtos.CreateOrEditQuestionRequestBody;
import it.epicode.focufy.entities.Question;
import it.epicode.focufy.exceptions.NotFoundException;
import it.epicode.focufy.repositories.QuestionRepo;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepo questionRepo;

    public Page<Question> getAllQuestions(int page, int size, String sortBy){
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return questionRepo.findAll(pageable);
    }

    public Optional<Question> getQuestionById(int id){
        return questionRepo.findById(id);
    }

    public String saveQuestion(CreateOrEditQuestionRequestBody questionRequestBody){
        Question questionToSave = new Question();
        questionToSave.setQuestionType(questionRequestBody.getQuestionType());
        questionToSave.setQuestionText(questionRequestBody.getQuestionText());
        questionRepo.save(questionToSave);
        return "Question with id=" + questionToSave.getId() + " correctly saved.";
    }

    public Question updateQuestion(int id, CreateOrEditQuestionRequestBody questionRequestBody){
        Optional<Question> questionOptional = getQuestionById(id);
        if(questionOptional.isPresent()){
            Question questionToUpdate = questionOptional.get();
            questionToUpdate.setQuestionType(questionRequestBody.getQuestionType());
            questionToUpdate.setQuestionText(questionRequestBody.getQuestionText());
            return questionRepo.save(questionToUpdate);
        } else {
            throw new NotFoundException("Question with id=" + id + " not found.");
        }
    }

    public String deleteQuestion(int id){
        Optional<Question> questionOptional = getQuestionById(id);
        if(questionOptional.isPresent()){
            Question questionToDelete = questionOptional.get();
            questionRepo.delete(questionToDelete);
            return "Question with id=" + id + " correctly deleted.";
        } else {
            throw new NotFoundException("Question with id=" + id + " not found.");
        }
    }
}
