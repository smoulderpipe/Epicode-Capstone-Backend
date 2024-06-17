package it.epicode.focufy.services;
import it.epicode.focufy.dtos.CreateOrEditAnswer;
import it.epicode.focufy.entities.Answer;
import it.epicode.focufy.entities.Question;
import it.epicode.focufy.entities.User;
import it.epicode.focufy.exceptions.NotFoundException;
import it.epicode.focufy.repositories.AnswerRepo;
import it.epicode.focufy.repositories.QuestionRepo;
import it.epicode.focufy.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AnswerService {

    @Autowired
    private AnswerRepo answerRepo;

    @Autowired
    private QuestionRepo questionRepo;

    @Autowired
    private UserRepo userRepo;

    public Page<Answer> getAllAnswers(int page, int size, String sortBy){
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return answerRepo.findAll(pageable);
    }

    public Optional<Answer> getAnswerById(int id){
        return answerRepo.findById(id);
    }

    public String saveAnswer(CreateOrEditAnswer answerRequestBody){
        Answer answerToSave = new Answer();
        answerToSave.setAnswerType(answerRequestBody.getAnswerType());
        answerToSave.setAnswerText(answerRequestBody.getAnswerText());
        answerToSave.setNumericAnswer(answerRequestBody.getNumericAnswer());
        answerToSave.setMultipleChoice(answerRequestBody.isMultipleChoice());

        Optional<Question> questionOptional = questionRepo.findById(answerRequestBody.getQuestionId());
        if(!questionOptional.isPresent()){
            throw new NotFoundException("Question with id=" + answerRequestBody.getQuestionId() + " not found.");
        }
        Question questionToAssign = questionOptional.get();
        answerToSave.setQuestion(questionToAssign);

        if(answerRequestBody.getUserId() != 0){
            Optional<User> userOptional = userRepo.findById(answerRequestBody.getUserId());
            if(!userOptional.isPresent()){
                throw new NotFoundException("User with id=" + answerRequestBody.getUserId() + " not found");
            }
            User userToAssign = userOptional.get();
            answerToSave.setUser(userToAssign);
        }

        answerRepo.save(answerToSave);
        return "Answer with id=" + answerToSave.getId() + " correctly saved.";
    }

    public Answer updateAnswer(int id, CreateOrEditAnswer answerRequestBody) {
        Optional<Answer> answerOptional = getAnswerById(id);
        if(answerOptional.isPresent()){
            Answer answerToUpdate = answerOptional.get();
            answerToUpdate.setAnswerType(answerRequestBody.getAnswerType());
            answerToUpdate.setAnswerText(answerRequestBody.getAnswerText());
            answerToUpdate.setNumericAnswer(answerRequestBody.getNumericAnswer());
            answerToUpdate.setMultipleChoice(answerRequestBody.isMultipleChoice());

            Optional<Question> questionOptional = questionRepo.findById(answerRequestBody.getQuestionId());
            if(!questionOptional.isPresent()){
                throw new NotFoundException("Question with id=" + answerRequestBody.getQuestionId() + " not found.");
            }
            Question questionToAssign = questionOptional.get();
            answerToUpdate.setQuestion(questionToAssign);

            if(answerRequestBody.getUserId() != 0) {
                Optional<User> userOptional = userRepo.findById(answerRequestBody.getUserId());
                if (!userOptional.isPresent()) {
                    throw new NotFoundException("User with id=" + answerRequestBody.getUserId() + " not found");
                }
                User userToAssign = userOptional.get();
                answerToUpdate.setUser(userToAssign);
            }
            return answerRepo.save(answerToUpdate);
        } else {
            throw new NotFoundException("Answer with id=" + id + " not found");
        }
    }

    public String deleteAnswer(int id){
        Optional<Answer> answerOptional = getAnswerById(id);
        if(answerOptional.isPresent()){
            Answer answerToDelete = answerOptional.get();
            answerRepo.delete(answerToDelete);
            return "Answer with id=" + id + "correctly deleted.";
        } else {
            throw new NotFoundException("Answer with id=" + id + " not found");
        }
    }

    public void saveAll(List<Answer> answers){
        answerRepo.saveAll(answers);
    }

    public boolean hasUserCompletedAllQuestions(int id){
        long totalQuestions = questionRepo.count();
        long userAnswers = answerRepo.countByUserId(id);

        return totalQuestions == userAnswers;
    }
}
