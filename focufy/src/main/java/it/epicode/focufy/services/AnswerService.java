package it.epicode.focufy.services;
import it.epicode.focufy.dtos.PersonalAnswerDTO;
import it.epicode.focufy.dtos.SharedAnswerDTO;
import it.epicode.focufy.entities.*;
import it.epicode.focufy.entities.enums.SharedAnswerType;
import it.epicode.focufy.exceptions.BadRequestException;
import it.epicode.focufy.exceptions.NotFoundException;
import it.epicode.focufy.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AnswerService {

    @Autowired
    private PersonalAnswerRepo personalAnswerRepo;

    @Autowired
    private SharedAnswerRepo sharedAnswerRepo;

    @Autowired
    private QuestionRepo questionRepo;

    @Autowired
    private UserRepo userRepo;

    public Page<PersonalAnswer> getAllPersonalAnswers(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return personalAnswerRepo.findAll(pageable);
    }

    public Page<SharedAnswer> getAllSharedAnswers(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return sharedAnswerRepo.findAll(pageable);
    }


    public Optional<PersonalAnswer> getPersonalAnswerById(int id) {
        return personalAnswerRepo.findById(id);
    }

    public Optional<SharedAnswer> getSharedAnswerById(int id) {
        return sharedAnswerRepo.findById(id);
    }

    public String savePersonalAnswer(PersonalAnswerDTO answerRequestBody) {
        PersonalAnswer answerToSave = new PersonalAnswer();
        populatePersonalAnswerFields(answerToSave, answerRequestBody);
        personalAnswerRepo.save(answerToSave);

        if (answerRequestBody.getUserId() != null) {
            assignAnswerToUser(answerRequestBody.getUserId(), answerToSave);
        }

        return "PersonalAnswer with id=" + answerToSave.getId() + " correctly saved.";
    }

    public String saveSharedAnswer(SharedAnswerDTO answerRequestBody) {
        SharedAnswer answerToSave = new SharedAnswer();
        populateSharedAnswerFields(answerToSave, answerRequestBody);
        sharedAnswerRepo.save(answerToSave);
        return "SharedAnswer with id=" + answerToSave.getId() + " correctly saved.";
    }

    public void assignSharedAnswerToUser(int sharedAnswerId, int userId) {
        SharedAnswer sharedAnswer = sharedAnswerRepo.findById(sharedAnswerId)
                .orElseThrow(() -> new NotFoundException("SharedAnswer with id=" + sharedAnswerId + " not found"));
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found"));
        sharedAnswer.getUsers().add(user);
        user.getSharedAnswers().add(sharedAnswer);
        sharedAnswerRepo.save(sharedAnswer);
        userRepo.save(user);
    }

    private void populatePersonalAnswerFields(PersonalAnswer answerToSave, PersonalAnswerDTO answerRequestBody) {
        answerToSave.setAnswerText(answerRequestBody.getAnswerText());
        answerToSave.setTimeDays(answerRequestBody.getTimeDays());
        answerToSave.setShortTermGoal(answerRequestBody.getShortTermGoal());
        answerToSave.setLongTermGoal(answerRequestBody.getLongTermGoal());
        answerToSave.setSatisfaction(answerRequestBody.getSatisfaction());
        answerToSave.setRestart(answerRequestBody.isRestart());

        Optional<Question> questionOptional = questionRepo.findById(answerRequestBody.getQuestionId());
        if (!questionOptional.isPresent()) {
            throw new NotFoundException("Question with id=" + answerRequestBody.getQuestionId() + " not found.");
        }
        answerToSave.setQuestion(questionOptional.get());
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


    private void assignAnswerToUser(Integer userId, Answer answer) {
        Optional<User> userOptional = userRepo.findById(userId);
        if (!userOptional.isPresent()) {
            throw new NotFoundException("User with id=" + userId + " not found");
        }
        User userToAssign = userOptional.get();
        if (answer instanceof PersonalAnswer) {
            userToAssign.getPersonalAnswers().add((PersonalAnswer) answer);
        } else if (answer instanceof SharedAnswer) {
            userToAssign.getSharedAnswers().add((SharedAnswer) answer);
        }
        userRepo.save(userToAssign);
    }

    public PersonalAnswer updatePersonalAnswer(int id, PersonalAnswerDTO answerRequestBody) {
        Optional<PersonalAnswer> answerOptional = getPersonalAnswerById(id);
        if (answerOptional.isPresent()) {
            PersonalAnswer answerToUpdate = answerOptional.get();
            populatePersonalAnswerFields(answerToUpdate, answerRequestBody);
            return personalAnswerRepo.save(answerToUpdate);
        } else {
            throw new NotFoundException("PersonalAnswer with id=" + id + " not found");
        }
    }

    public SharedAnswer updateSharedAnswer(int id, SharedAnswerDTO answerRequestBody) {
        Optional<SharedAnswer> answerOptional = getSharedAnswerById(id);
        if (answerOptional.isPresent()) {
            SharedAnswer answerToUpdate = answerOptional.get();
            populateSharedAnswerFields(answerToUpdate, answerRequestBody);
            return sharedAnswerRepo.save(answerToUpdate);
        } else {
            throw new NotFoundException("SharedAnswer with id=" + id + " not found");
        }
    }

    public String deletePersonalAnswer(int id) {
        Optional<PersonalAnswer> answerOptional = getPersonalAnswerById(id);
        if (answerOptional.isPresent()) {
            PersonalAnswer answerToDelete = answerOptional.get();
            personalAnswerRepo.delete(answerToDelete);
            return "PersonalAnswer with id=" + id + " correctly deleted.";
        } else {
            throw new NotFoundException("PersonalAnswer with id=" + id + " not found");
        }
    }

    public String deleteSharedAnswer(int id) {
        Optional<SharedAnswer> answerOptional = getSharedAnswerById(id);
        if (answerOptional.isPresent()) {
            SharedAnswer answerToDelete = answerOptional.get();
            sharedAnswerRepo.delete(answerToDelete);
            return "SharedAnswer with id=" + id + " correctly deleted.";
        } else {
            throw new NotFoundException("SharedAnswer with id=" + id + " not found");
        }
    }

    public boolean hasUserCompletedAllQuestions(int id) {
        long totalQuestions = questionRepo.count();
        long userPersonalAnswers = personalAnswerRepo.countByUser_Id(id);
        long userSharedAnswers = sharedAnswerRepo.countByUsers_Id(id);
        return totalQuestions == (userPersonalAnswers + userSharedAnswers);
    }

    public void loadSharedAnswersFromFile(BufferedReader reader) throws IOException {
        List<SharedAnswer> sharedAnswers = new ArrayList<>();

        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",", 4); // Considerando un campo in più per sharedAnswerType
            if (parts.length != 4) {
                throw new BadRequestException("Invalid format in input file");
            }

            String answerText = parts[0].trim();
            int questionId = Integer.parseInt(parts[1].trim());
            SharedAnswerType sharedAnswerType = SharedAnswerType.valueOf(parts[2].trim()); // Parsing sharedAnswerType
            int userId = Integer.parseInt(parts[3].trim());

            Optional<User> userOptional = userRepo.findById(userId);
            if (!userOptional.isPresent()) {
                throw new BadRequestException("User with id=" + userId + " not found");
            }

            Optional<Question> questionOptional = questionRepo.findById(questionId);
            if (!questionOptional.isPresent()) {
                throw new BadRequestException("Question with id=" + questionId + " not found");
            }

            SharedAnswer sharedAnswer = new SharedAnswer();
            sharedAnswer.setAnswerText(answerText);
            sharedAnswer.setSharedAnswerType(sharedAnswerType);
            sharedAnswer.setQuestion(questionOptional.get());
            sharedAnswer.getUsers().add(userOptional.get()); // Assign the user to the shared answer

            sharedAnswers.add(sharedAnswer);
        }

        sharedAnswerRepo.saveAll(sharedAnswers);
    }
}
