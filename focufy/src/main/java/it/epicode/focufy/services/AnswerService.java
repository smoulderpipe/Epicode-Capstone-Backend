package it.epicode.focufy.services;
import it.epicode.focufy.dtos.PersonalAnswerDTO;
import it.epicode.focufy.dtos.SharedAnswerDTO;
import it.epicode.focufy.entities.*;
import it.epicode.focufy.entities.enums.QuestionType;
import it.epicode.focufy.entities.enums.SharedAnswerType;
import it.epicode.focufy.exceptions.BadRequestException;
import it.epicode.focufy.exceptions.NotFoundException;
import it.epicode.focufy.exceptions.UnauthorizedException;
import it.epicode.focufy.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @Autowired
    private AvatarService avatarService;

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

    public Page<SharedAnswer> getOwnSharedAnswers(int userId, int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        List<SharedAnswer> userSharedAnswers = sharedAnswerRepo.findByUsers_Id(userId);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), userSharedAnswers.size());
        Page<SharedAnswer> sharedAnswersPage = new PageImpl<>(userSharedAnswers.subList(start, end), pageable, userSharedAnswers.size());
        return sharedAnswersPage;
    }

    public Page<PersonalAnswer> getOwnPersonalAnswers(int userId, int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        List<PersonalAnswer> userPersonalAnswers = personalAnswerRepo.findByUserId(userId);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), userPersonalAnswers.size());
        Page<PersonalAnswer> personalAnswerPage = new PageImpl<>(userPersonalAnswers.subList(start, end), pageable, userPersonalAnswers.size());
        return personalAnswerPage;
    }
    public String savePersonalAnswer(PersonalAnswerDTO answerRequestBody) {
        PersonalAnswer answerToSave = new PersonalAnswer();
        populatePersonalAnswerFields(answerToSave, answerRequestBody);

        User user = userRepo.findById(answerRequestBody.getUserId())
                .orElseThrow(() ->
                        new NotFoundException("User with id=" + answerRequestBody.getUserId() + " not found"));
            answerToSave.setUser(user);

            if (user.getPersonalAnswers() == null) {
                user.setPersonalAnswers(new ArrayList<>());
            }
            user.getPersonalAnswers().add(answerToSave);


        personalAnswerRepo.save(answerToSave);
        return "PersonalAnswer with id=" + answerToSave.getId() + " correctly saved for user with id=" + answerRequestBody.getUserId();
    }

    public String saveSharedAnswer(SharedAnswerDTO answerRequestBody) {
        SharedAnswer answerToSave = new SharedAnswer();
        populateSharedAnswerFields(answerToSave, answerRequestBody);
        sharedAnswerRepo.save(answerToSave);
        return "SharedAnswer with id=" + answerToSave.getId() + " correctly saved.";
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

    public void assignPersonalAnswerToUser(int userId, PersonalAnswer personalAnswer) {
        Optional<User> userOptional = userRepo.findById(userId);
        if (!userOptional.isPresent()) {
            throw new NotFoundException("User with id=" + userId + " not found");
        }
        User userToAssign = userOptional.get();
        personalAnswer.setUser(userToAssign);
        if (userToAssign.getPersonalAnswers() == null) {
            userToAssign.setPersonalAnswers(new ArrayList<>());
        }
        userToAssign.getPersonalAnswers().add(personalAnswer);
        personalAnswerRepo.save(personalAnswer);
        userRepo.save(userToAssign);
    }

    public void assignSharedAnswerToUser(int sharedAnswerId, int userId) {
        SharedAnswer sharedAnswer = sharedAnswerRepo.findById(sharedAnswerId)
                .orElseThrow(() -> new NotFoundException("SharedAnswer with id=" + sharedAnswerId + " not found"));
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found"));

        if (!sharedAnswer.getUsers().contains(user)) {
            sharedAnswer.getUsers().add(user);
            sharedAnswerRepo.save(sharedAnswer);
        }

        if (!user.getSharedAnswers().contains(sharedAnswer)) {
            user.getSharedAnswers().add(sharedAnswer);
            userRepo.save(user);
        }

        if (hasCompletedAllSharedQuestions(userId) && !avatarService.isAvatarAssigned(userId)) {
            avatarService.assignAvatarToUser(userId);
        }
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

    public boolean hasCompletedAllSharedQuestions(int userId) {
        long userSharedAnswers = sharedAnswerRepo.countByUsers_Id(userId);
        long totalSharedQuestions = getTotalSharedQuestions();
        return userSharedAnswers == totalSharedQuestions;
    }

    private long getTotalSharedQuestions() {
        return questionRepo.countByQuestionType(QuestionType.CHRONOTYPE)
                + questionRepo.countByQuestionType(QuestionType.TEMPER);
    }

    public void loadSharedAnswersFromFile(BufferedReader reader) throws IOException {
        List<SharedAnswer> sharedAnswers = new ArrayList<>();

        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",", 3);
            if (parts.length != 3) {
                throw new BadRequestException("Invalid format in input file");
            }

            String answerText = parts[0].trim();
            int questionId = Integer.parseInt(parts[1].trim());
            SharedAnswerType sharedAnswerType = SharedAnswerType.valueOf(parts[2].trim());

            Optional<Question> questionOptional = questionRepo.findById(questionId);
            if (!questionOptional.isPresent()) {
                throw new BadRequestException("Question with id=" + questionId + " not found");
            }

            SharedAnswer sharedAnswer = new SharedAnswer();
            sharedAnswer.setAnswerText(answerText);
            sharedAnswer.setSharedAnswerType(sharedAnswerType);
            sharedAnswer.setQuestion(questionOptional.get());
            sharedAnswer.setUsers(new ArrayList<>());

            sharedAnswers.add(sharedAnswer);
        }

        sharedAnswerRepo.saveAll(sharedAnswers);
    }

    public void clearUserSharedAnswers(int userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int authenticatedUserId = ((User) authentication.getPrincipal()).getId();

        if (authenticatedUserId != userId) {
            throw new UnauthorizedException("You are not allowed to clear shared answers for another user.");
        }

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found."));

        user.getSharedAnswers().clear();
        userRepo.save(user);
    }

}
