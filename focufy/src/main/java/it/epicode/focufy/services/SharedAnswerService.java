package it.epicode.focufy.services;

import it.epicode.focufy.dtos.SharedAnswerDTO;
import it.epicode.focufy.entities.Question;
import it.epicode.focufy.entities.SharedAnswer;
import it.epicode.focufy.exceptions.NotFoundException;
import it.epicode.focufy.repositories.QuestionRepo;
import it.epicode.focufy.repositories.SharedAnswerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SharedAnswerService {

    @Autowired
    private SharedAnswerRepo sharedAnswerRepo;

    @Autowired
    private QuestionRepo questionRepo;

    public Page<SharedAnswer> getAllSharedAnswers(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return sharedAnswerRepo.findAll(pageable);
    }

    public Optional<SharedAnswer> getSharedAnswerById(int id) {
        return sharedAnswerRepo.findById(id);
    }

    public String saveSharedAnswer(SharedAnswerDTO answerRequestBody) {
        SharedAnswer answerToSave = new SharedAnswer();
        populateSharedAnswerFields(answerToSave, answerRequestBody);
        sharedAnswerRepo.save(answerToSave);
        return "SharedAnswer with id=" + answerToSave.getId() + " correctly saved.";
    }

    private void populateSharedAnswerFields(SharedAnswer answerToSave, SharedAnswerDTO answerRequestBody) {
        answerToSave.setAnswerText(answerRequestBody.getAnswerText());
        answerToSave.setSharedAnswerType(answerRequestBody.getSharedAnswerType());

        Optional<Question> questionOptional = questionRepo.findById(answerRequestBody.getQuestionId());
        if (!questionOptional.isPresent()) {
            throw new NotFoundException("Question with id=" + answerRequestBody.getQuestionId() + " not found.");
        }
        answerToSave.setQuestion(questionOptional.get());
    }
}
