package it.epicode.focufy.services;
import it.epicode.focufy.dtos.CheckpointAnswerDTO;
import it.epicode.focufy.dtos.DeadlineAnswerDTO;
import it.epicode.focufy.dtos.PersonalAnswerDTO;
import it.epicode.focufy.dtos.SharedAnswerDTO;
import it.epicode.focufy.entities.*;
import it.epicode.focufy.entities.enums.CDAnswerType;
import it.epicode.focufy.entities.enums.PersonalAnswerType;
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
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    private CheckpointAnswerRepo checkpointAnswerRepo;

    @Autowired
    private DeadlineAnswerRepo deadlineAnswerRepo;

    @Autowired
    private CheckpointDayRepo checkpointDayRepo;

    @Autowired
    private DeadlineDayRepo deadlineDayRepo;

    @Autowired
    private StudyPlanService studyPlanService;

    @Autowired
    private UserService userService;

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

    public Optional<SharedAnswerDTO> getSharedAnswerById(int id) {
        return sharedAnswerRepo.findById(id).map(this::convertSToDTO);
    }

    public SharedAnswerDTO convertSToDTO(SharedAnswer sharedAnswer){
        return new SharedAnswerDTO(
                sharedAnswer.getId(),
                sharedAnswer.getSharedAnswerType(),
                sharedAnswer.getAnswerText()
        );
    }

    public List<SharedAnswer> getSharedAnswersForQuestion(int questionId){
        return sharedAnswerRepo.findByQuestionId(questionId);
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


    @Transactional
    public String savePersonalAnswers(List<PersonalAnswerDTO> personalAnswerDTOs) {
        for (PersonalAnswerDTO dto : personalAnswerDTOs) {
            User user = userRepo.findById(dto.getUserId())
                    .orElseThrow(() -> new NotFoundException("User with id=" + dto.getUserId() + " not found."));

            PersonalAnswer personalAnswer = new PersonalAnswer();
            populatePersonalAnswerFields(personalAnswer, dto, user);

            if (user.getPersonalAnswers() == null) {
                user.setPersonalAnswers(new ArrayList<>());
            }
            user.getPersonalAnswers().add(personalAnswer);
            personalAnswerRepo.save(personalAnswer);

            if (dto.getPersonalAnswerType() == PersonalAnswerType.RESTART) {
                clearUserPersonalAnswers(user.getId());
                clearUserSharedAnswers(user.getId());
                clearUserCheckpointAnswers(user.getId());
                clearUserDeadlineAnswers(user.getId());
                userService.updateUserLongTermGoal(user.getId(), null);
                avatarService.removeAvatarAssignment(user.getId());
                studyPlanService.deleteStudyPlanByUserId(user.getId());
            }
        }
        return "Personal answers saved successfully.";
    }

    public String saveSharedAnswer(SharedAnswerDTO answerRequestBody) {
        SharedAnswer answerToSave = new SharedAnswer();
        populateSharedAnswerFields(answerToSave, answerRequestBody);
        sharedAnswerRepo.save(answerToSave);
        return "SharedAnswer with id=" + answerToSave.getId() + " correctly saved.";
    }

    private void populatePersonalAnswerFields(PersonalAnswer personalAnswer, PersonalAnswerDTO personalAnswerDTO, User user) {
        personalAnswer.setUser(user);
        personalAnswer.setPersonalAnswerType(personalAnswerDTO.getPersonalAnswerType());
        personalAnswer.setAnswerText(personalAnswerDTO.getAnswerText());
        personalAnswer.setPersonalAnswerType(personalAnswerDTO.getPersonalAnswerType());

        Question question = questionRepo.findById(personalAnswerDTO.getQuestionId())
                .orElseThrow(() -> new NotFoundException("Question with id=" + personalAnswerDTO.getQuestionId() + " not found."));
        personalAnswer.setQuestion(question);
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
        User user = userRepo.findById(answerRequestBody.getUserId())
                .orElseThrow(() -> new NotFoundException("User with id=" + answerRequestBody.getUserId() + " not found."));
        Optional<PersonalAnswer> answerOptional = getPersonalAnswerById(id);
        if (answerOptional.isPresent()) {
            PersonalAnswer answerToUpdate = answerOptional.get();
            populatePersonalAnswerFields(answerToUpdate, answerRequestBody, user);
            return personalAnswerRepo.save(answerToUpdate);
        } else {
            throw new NotFoundException("PersonalAnswer with id=" + id + " not found");
        }
    }

    public SharedAnswer updateSharedAnswer(int id, SharedAnswerDTO answerRequestBody) {
        Optional<SharedAnswer> answerOptional = sharedAnswerRepo.findById(id);
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
            Question question = answerToDelete.getQuestion();
            question.getAnswers().remove(answerToDelete);

            personalAnswerRepo.delete(answerToDelete);

            return "PersonalAnswer with id=" + id + " correctly deleted.";
        } else {
            throw new NotFoundException("PersonalAnswer with id=" + id + " not found");
        }
    }


    public String deleteSharedAnswer(int id) {
        Optional<SharedAnswer> answerOptional = sharedAnswerRepo.findById(id);
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

        List<SharedAnswer> sharedAnswersToRemove = user.getSharedAnswers();
        System.out.println("SharedAnswers to remove: " + sharedAnswersToRemove.size());

        for (SharedAnswer sharedAnswer : sharedAnswersToRemove) {
            sharedAnswer.getUsers().remove(user);
            sharedAnswerRepo.save(sharedAnswer);
        }

        user.getSharedAnswers().clear();
        userRepo.save(user);
    }

    public void clearUserPersonalAnswers(int userId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int authenticatedUserId = ((User) authentication.getPrincipal()).getId();

        System.out.println("Authenticated User ID: " + authenticatedUserId);
        System.out.println("User ID from request: " + userId);

        if (authenticatedUserId != userId) {
            throw new UnauthorizedException("You are not allowed to clear shared answers for another user.");
        }
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found."));

        List<PersonalAnswer> personalAnswers = personalAnswerRepo.findByUserId(userId);

        personalAnswerRepo.deleteAll(personalAnswers);

        for (PersonalAnswer personalAnswer : personalAnswers) {
            personalAnswer.getUser().getPersonalAnswers().remove(personalAnswer);
        }

        user.getPersonalAnswers().clear();
        userRepo.save(user);
    }

    public void clearUserCheckpointAnswers(int userId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int authenticatedUserId = ((User) authentication.getPrincipal()).getId();

        if (authenticatedUserId != userId) {
            throw new UnauthorizedException("You are not allowed to clear shared answers for another user.");
        }
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found."));

        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
        List<CheckpointAnswer> checkpointAnswers = checkpointAnswerRepo.findByUserId(userId, pageable).getContent();

        checkpointAnswerRepo.deleteAll(checkpointAnswers);
        for (CheckpointAnswer checkpointAnswer : checkpointAnswers) {
            checkpointAnswer.getUser().getCheckpointAnswers().remove(checkpointAnswer);
        }

        user.getCheckpointAnswers().clear();
        userRepo.save(user);
    }

    public void clearUserDeadlineAnswers(int userId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int authenticatedUserId = ((User) authentication.getPrincipal()).getId();

        if (authenticatedUserId != userId) {
            throw new UnauthorizedException("You are not allowed to clear shared answers for another user.");
        }
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found."));

        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
        List<DeadlineAnswer> deadlineAnswers = deadlineAnswerRepo.findByUserId(userId, pageable).getContent();

        deadlineAnswerRepo.deleteAll(deadlineAnswers);

        user.getDeadlineAnswers().clear();
        userRepo.save(user);
    }

    @Transactional
    public List<CheckpointAnswerDTO> saveCheckpointAnswers(List<CheckpointAnswerDTO> checkpointAnswerDTOs) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int authenticatedUserId = ((User) authentication.getPrincipal()).getId();
        List<CheckpointAnswerDTO> savedAnswersDTO = new ArrayList<>();

        for (CheckpointAnswerDTO dto : checkpointAnswerDTOs) {
            try {
                User user = userRepo.findById(authenticatedUserId)
                        .orElseThrow(() -> new NotFoundException("User with id=" + authenticatedUserId + " not found."));

                int countAnswers = checkpointAnswerRepo.countByUserIdAndCheckpointDayId(authenticatedUserId, dto.getCheckpointDayId());
                if (countAnswers >= 3) {
                    throw new IllegalStateException("Maximum number of checkpoint answers exceeded for this day.");
                }

                CheckpointAnswer checkpointAnswer = new CheckpointAnswer();
                populateCheckpointAnswerFields(checkpointAnswer, dto, user);

                if (user.getCheckpointAnswers() == null) {
                    user.setCheckpointAnswers(new ArrayList<>());
                }
                user.getCheckpointAnswers().add(checkpointAnswer);
                checkpointAnswerRepo.save(checkpointAnswer);

                savedAnswersDTO.add(convertCToDTO(checkpointAnswer));
            } catch (NotFoundException e) {
                System.err.println("Error: " + e.getMessage());
                throw e;
            } catch (IllegalStateException e) {
                System.err.println("Error: " + e.getMessage());
                throw e;
            } catch (Exception e) {
                System.err.println("Unexpected error: " + e.getMessage());
                throw new RuntimeException("Unexpected error occurred", e);
            }
        }

        return savedAnswersDTO;
    }


    private CheckpointAnswerDTO convertCToDTO(CheckpointAnswer checkpointAnswer) {
        CheckpointAnswerDTO dto = new CheckpointAnswerDTO();
        dto.setQuestionId(checkpointAnswer.getQuestion().getId());
        dto.setAnswerText(checkpointAnswer.getAnswerText());
        dto.setCheckpointDayId(checkpointAnswer.getCheckpointDay().getId());
        dto.setUserId(checkpointAnswer.getUser().getId());
        dto.setAnswerType(checkpointAnswer.getAnswerType());
        return dto;
    }

    private void populateCheckpointAnswerFields(CheckpointAnswer checkpointAnswer, CheckpointAnswerDTO checkpointAnswerDTO, User user) {
        checkpointAnswer.setUser(user);
        checkpointAnswer.setAnswerText(checkpointAnswerDTO.getAnswerText());
        checkpointAnswer.setAnswerType(checkpointAnswerDTO.getAnswerType());

        CheckpointDay checkpointDay = checkpointDayRepo.findById(checkpointAnswerDTO.getCheckpointDayId())
                .orElseThrow(()-> new NotFoundException("Checkpoint Day with id=" + checkpointAnswerDTO.getCheckpointDayId() + " not found."));
        checkpointAnswer.setCheckpointDay(checkpointDay);

        Question question = questionRepo.findById(checkpointAnswerDTO.getQuestionId())
                .orElseThrow(() -> new NotFoundException("Question with id=" + checkpointAnswerDTO.getQuestionId() + " not found."));
        checkpointAnswer.setQuestion(question);
    }

    public Page<CheckpointAnswer> getOwnCheckpointAnswers(int userId, int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return checkpointAnswerRepo.findByUserId(userId, pageable);
    }

    public List<DeadlineAnswerDTO> saveDeadlineAnswers(List<DeadlineAnswerDTO> deadlineAnswerDTOs) {
        List<DeadlineAnswerDTO> savedAnswersDTO = new ArrayList<>();

        for (DeadlineAnswerDTO dto : deadlineAnswerDTOs) {
            try {
                User user = userRepo.findById(dto.getUserId())
                        .orElseThrow(() -> new NotFoundException("User with id=" + dto.getUserId() + " not found."));

                int countAnswers = deadlineAnswerRepo.countByUserIdAndDeadlineDayId(dto.getUserId(), dto.getDeadlineDayId());
                if (countAnswers >= 4) {
                    throw new IllegalStateException("Maximum number of deadline answers exceeded for this day.");
                }

                DeadlineAnswer deadlineAnswer = new DeadlineAnswer();
                populateDeadlineAnswerFields(deadlineAnswer, dto, user);

                if (user.getDeadlineAnswers() == null) {
                    user.setDeadlineAnswers(new ArrayList<>());
                }
                user.getDeadlineAnswers().add(deadlineAnswer);
                deadlineAnswerRepo.save(deadlineAnswer);

                savedAnswersDTO.add(convertDToDTO(deadlineAnswer));
            } catch (NotFoundException e) {
                System.err.println("Error: " + e.getMessage());
                throw e;
            } catch (IllegalStateException e) {
                System.err.println("Error: " + e.getMessage());
                throw e;
            } catch (Exception e) {
                System.err.println("Unexpected error: " + e.getMessage());
                throw new RuntimeException("Unexpected error occurred", e);
            }
        }

        return savedAnswersDTO;
    }

    private DeadlineAnswerDTO convertDToDTO(DeadlineAnswer deadlineAnswer) {
        DeadlineAnswerDTO dto = new DeadlineAnswerDTO();
        dto.setQuestionId(deadlineAnswer.getQuestion().getId());
        dto.setAnswerText(deadlineAnswer.getAnswerText());
        dto.setDeadlineDayId(deadlineAnswer.getDeadlineDay().getId());
        dto.setUserId(deadlineAnswer.getUser().getId());
        dto.setAnswerType(deadlineAnswer.getAnswerType());
        return dto;
    }

    private void populateDeadlineAnswerFields(DeadlineAnswer deadlineAnswer, DeadlineAnswerDTO deadlineAnswerDTO, User user) {
        deadlineAnswer.setUser(user);
        deadlineAnswer.setAnswerText(deadlineAnswerDTO.getAnswerText());
        deadlineAnswer.setAnswerType(deadlineAnswerDTO.getAnswerType());

        DeadlineDay deadlineDay = deadlineDayRepo.findById(deadlineAnswerDTO.getDeadlineDayId())
                .orElseThrow(()-> new NotFoundException("Deadline Day with id=" + deadlineAnswerDTO.getDeadlineDayId() + " not found."));
        deadlineAnswer.setDeadlineDay(deadlineDay);

        Question question = questionRepo.findById(deadlineAnswerDTO.getQuestionId())
                .orElseThrow(() -> new NotFoundException("Question with id=" + deadlineAnswerDTO.getQuestionId() + " not found."));
        deadlineAnswer.setQuestion(question);
    }

    public Page<CheckpointAnswer> getCheckpointAnswersByCheckpointDayId(int checkpointDayId, int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return checkpointAnswerRepo.findByCheckpointDay_Id(checkpointDayId, pageable);
    }

    public Page<DeadlineAnswer> getOwnDeadlineAnswers(int userId, int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return deadlineAnswerRepo.findByUserId(userId, pageable);
    }

    public Page<DeadlineAnswer> getDeadlineAnswersByDeadlineDayId(int deadlineDayId, int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return deadlineAnswerRepo.findByDeadlineDay_Id(deadlineDayId, pageable);
    }

    public List<CheckpointAnswerDTO> getCheckpointAnswersByTypeAndUserId(CDAnswerType cdAnswerType, int userId) {
        List<CheckpointAnswer> checkpointAnswers = checkpointAnswerRepo.findByAnswerTypeAndUserId(cdAnswerType, userId);
        return checkpointAnswers.stream()
                .map(this::convertCToDTO)
                .collect(Collectors.toList());
    }

    public List<DeadlineAnswerDTO> getDeadlineAnswersByTypeAndUserId(CDAnswerType cdAnswerType, int userId) {
        List<DeadlineAnswer> deadlineAnswers = deadlineAnswerRepo.findByAnswerTypeAndUserId(cdAnswerType, userId);
        return deadlineAnswers.stream()
                .map(this::convertDToDTO)
                .collect(Collectors.toList());
    }



}
