package it.epicode.focufy.services;
import it.epicode.focufy.dtos.*;
import it.epicode.focufy.entities.*;
import it.epicode.focufy.entities.enums.*;
import it.epicode.focufy.exceptions.NotFoundException;
import it.epicode.focufy.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudyPlanService {

    @Autowired
    private StudyPlanRepo studyPlanRepo;

    @Autowired
    private MantraRepo mantraRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private StudyDayRepo studyDayRepo;

    @Autowired
    private CheckpointDayRepo checkpointDayRepo;

    @Autowired
    private DeadlineDayRepo deadlineDayRepo;

    @Autowired
    private ActivitySessionRepo activitySessionRepo;

    @Autowired
    private ActivitySessionService activitySessionService;

    @Autowired
    private QuestionRepo questionRepo;

    @Autowired
    private DayService dayService;

    @Transactional
    public StudyPlan saveStudyPlanAndCreateDays(StudyPlanDTO studyPlanDTO) {
        User user = userRepo.findById(studyPlanDTO.getUserId())
                .orElseThrow(() -> new NotFoundException("User with id=" + studyPlanDTO.getUserId() + " not found."));

        StudyPlan studyPlan = new StudyPlan();
        studyPlan.setUser(user);
        studyPlan.setShortTermGoal(studyPlanDTO.getShortTermGoal());

        studyPlan = studyPlanRepo.save(studyPlan);

        List<Question> checkpointQuestions = questionRepo.findByQuestionType(QuestionType.CHECKPOINT);
        List<Question> restartQuestions = questionRepo.findByQuestionType(QuestionType.RESTART);
        List<Question> deadlineQuestions = questionRepo.findByQuestionType(QuestionType.DEADLINE);

        List<Day> days = new ArrayList<>();
        int numberOfDays = studyPlanDTO.getNumberOfDays();
        LocalDate currentDate = LocalDate.now();
        int dayCounter = 1;

        Day lastDay = null;

        for (int i = 0; i < numberOfDays; i++) {
            String dayName = "DAY " + dayCounter;
            LocalDate dayDate = currentDate.plusDays(i);
            dayCounter++;

            if (dayDate.getDayOfWeek() == DayOfWeek.SUNDAY && i != numberOfDays - 1) {
                CheckpointDay checkpointDay = new CheckpointDay();
                checkpointDay.setStudyPlan(studyPlan);
                checkpointDay.setName(dayName);
                checkpointDay.setDate(dayDate);
                checkpointDay = dayService.saveCheckpointDayWithQuestions(checkpointDay, checkpointQuestions, restartQuestions);
                days.add(checkpointDay);
            } else if (i == numberOfDays - 1) {
                DeadlineDay deadlineDay = new DeadlineDay();
                deadlineDay.setStudyPlan(studyPlan);
                deadlineDay.setName(dayName);
                deadlineDay.setDate(dayDate);
                deadlineDay = dayService.saveDeadlineDayWithQuestions(deadlineDay, deadlineQuestions, restartQuestions);
                days.add(deadlineDay);
            } else {
                StudyDay studyDay = new StudyDay();
                studyDay.setStudyPlan(studyPlan);
                studyDay.setName(dayName);
                studyDay.setDate(dayDate);
                addActivitySessionsToStudyDay(studyDay, user.getAvatar().getChronotype().getChronotypeType());
                studyDay = studyDayRepo.save(studyDay);
                days.add(studyDay);
            }
        }

        // Check if the last day is a Sunday and replace it with a deadline day if necessary
        if (days.get(days.size() - 1) instanceof CheckpointDay) {
            Day checkpointDay = days.remove(days.size() - 1);
            DeadlineDay deadlineDay = new DeadlineDay();
            deadlineDay.setStudyPlan(studyPlan);
            deadlineDay.setName(checkpointDay.getName());
            deadlineDay.setDate(checkpointDay.getDate());
            deadlineDay = dayService.saveDeadlineDayWithQuestions(deadlineDay, deadlineQuestions, restartQuestions);
            days.add(deadlineDay);
        }

        studyPlan.setDays(days);
        studyPlan = studyPlanRepo.save(studyPlan);
        user.setStudyPlan(studyPlan);
        userRepo.save(user);

        return studyPlan;
    }

    private void addActivitySessionsToStudyDay(StudyDay studyDay, ChronotypeType chronotype) {
        LocalTime studyStartTime = calculateStudyStartTime(chronotype);
        LocalTime restStartTime = calculateRestStartTime(chronotype);
        LocalTime funStartTime = calculateFunStartTime(chronotype);

        ActivitySession studySession = activitySessionService.saveActivitySession(ActivitySessionType.STUDY, 8, studyStartTime);
        ActivitySession restSession = activitySessionService.saveActivitySession(ActivitySessionType.REST, 8, restStartTime);
        ActivitySession funSession = activitySessionService.saveActivitySession(ActivitySessionType.FUN, 4, funStartTime);

        studySession.setStudyDay(studyDay);
        restSession.setStudyDay(studyDay);
        funSession.setStudyDay(studyDay);

        studyDay.getActivitySessions().add(studySession);
        studyDay.getActivitySessions().add(restSession);
        studyDay.getActivitySessions().add(funSession);
    }

    private LocalTime calculateStudyStartTime(ChronotypeType chronotype) {
        return switch (chronotype) {
            case LION -> LocalTime.of(7, 0);
            case BEAR -> LocalTime.of(10, 0);
            case DOLPHIN -> LocalTime.of(13, 0);
            case WOLF -> LocalTime.of(16, 0);
            default -> throw new IllegalArgumentException("Unknown chronotype");
        };
    }

    private LocalTime calculateRestStartTime(ChronotypeType chronotype) {
        return switch (chronotype) {
            case LION -> LocalTime.of(22, 0); // 22:00 (10:00 di sera)
            case BEAR -> LocalTime.of(23, 0); // 23:00 (11:00 di sera)
            case DOLPHIN -> LocalTime.of(1, 0); // 01:00 di notte
            case WOLF -> LocalTime.of(2, 0); // 02:00 di notte
            default -> throw new IllegalArgumentException("Unknown chronotype");
        };
    }

    private LocalTime calculateFunStartTime(ChronotypeType chronotype) {
        return switch (chronotype) {
            case LION -> LocalTime.of(17, 0);
            case BEAR -> LocalTime.of(19, 0);
            case DOLPHIN -> LocalTime.of(20, 0);
            case WOLF -> LocalTime.of(11, 0);
            default -> throw new IllegalArgumentException("Unknown chronotype");
        };
    }

    @Transactional
    public void addMantrasToStudyPlanByMantraType(int userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found."));

        RiskType riskType = user.getAvatar().getTemper().getRiskType();
        MantraType mantraType = mapRiskTypeToMantraType(riskType);
        List<Mantra> mantras = mantraRepo.findByMantraType(mantraType);

        StudyPlan studyPlan = user.getStudyPlan();
        if (studyPlan == null) {
            throw new NotFoundException("Studyplan not found for user with id=" + userId);
        }

        List<Day> days = studyPlan.getDays();

        if (days == null || days.isEmpty()) {
            throw new NotFoundException("No days found in the study plan for user with id=" + userId);
        }

        int mantraIndex = 0;
        int totalMantras = mantras.size();

        for (Day day : days) {
            if (day instanceof StudyDay) {
                StudyDay studyDay = (StudyDay) day;

                if (mantraIndex >= totalMantras){
                    mantraIndex = 0;
                }
                Mantra mantra = mantras.get(mantraIndex);
                studyDay.setMantra(mantra);
                mantraIndex++;
            }
        }

        studyPlanRepo.save(studyPlan);
    }

    private MantraType mapRiskTypeToMantraType(RiskType riskType) {
        return switch (riskType) {
            case DISTRACTION -> MantraType.DISTRACTION;
            case BOREDOM -> MantraType.BOREDOM;
            case OVERTHINKING -> MantraType.OVERTHINKING;
            case BURNOUT -> MantraType.BURNOUT;
            default -> throw new IllegalArgumentException("Unknown risk type");
        };
    }

    @Transactional
    public void deleteStudyPlanByUserId(int userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found."));
        StudyPlan studyPlan = user.getStudyPlan();
        if (studyPlan != null) {
            user.setStudyPlan(null);
            userRepo.save(user);
            List<Day> days = studyPlan.getDays();

            for (Day day : days) {
                if (day instanceof StudyDay) {
                    StudyDay studyDay = (StudyDay) day;
                    studyDay.setMantra(null);

                    for (ActivitySession session : studyDay.getActivitySessions()) {
                        activitySessionRepo.delete(session);
                    }
                    studyDayRepo.delete(studyDay);
                } else if (day instanceof CheckpointDay) {
                    CheckpointDay checkpointDay = (CheckpointDay) day;
                    checkpointDayRepo.delete(checkpointDay);
                }
            }

            studyPlanRepo.delete(studyPlan);
        } else {
            throw new NotFoundException("StudyPlan not found for user with id=" + userId);
        }
    }

    @Transactional(readOnly = true)
    public StudyPlanResponseDTO getStudyPlanByUserId(int userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found."));

        StudyPlan studyPlan = user.getStudyPlan();
        if (studyPlan == null) {
            throw new NotFoundException("Study plan not found for user with id=" + userId);
        }

        for (Day day : studyPlan.getDays()) {
            if (day instanceof CheckpointDay) {
                ((CheckpointDay) day).getQuestions().size();
            } else if (day instanceof DeadlineDay) {
                ((DeadlineDay) day).getQuestions().size();
            }
        }

        StudyPlanResponseDTO responseDTO = new StudyPlanResponseDTO(studyPlan);
        for (Day day : studyPlan.getDays()) {
            responseDTO.addDay(mapToDayDTO(day));
        }

        return responseDTO;
    }

    private DayDTO mapToDayDTO(Day day) {
        if (day instanceof StudyDay) {
            return mapToStudyDayDTO((StudyDay) day);
        } else if (day instanceof CheckpointDay) {
            return mapToCheckpointDayDTO((CheckpointDay) day);
        } else if (day instanceof DeadlineDay) {
            return mapToDeadlineDayDTO((DeadlineDay) day);
        }
        return null;
    }

    private StudyDayDTO mapToStudyDayDTO(StudyDay studyDay) {
        StudyDayDTO studyDayDTO = new StudyDayDTO();
        studyDayDTO.setId(studyDay.getId());
        studyDayDTO.setType("StudyDay");
        studyDayDTO.setName(studyDay.getName());
        studyDayDTO.setDate(studyDay.getDate());
        studyDayDTO.setMantra(studyDay.getMantra() != null ? studyDay.getMantra().getText() : null);
        studyDayDTO.setActivitySessions(studyDay.getActivitySessions().stream()
                .map(session -> new ActivitySessionDTO(session.getActivitySessionType(), session.getDuration(), session.getStartTime()))
                .collect(Collectors.toList()));
        return studyDayDTO;
    }

    private CheckpointDayDTO mapToCheckpointDayDTO(CheckpointDay checkpointDay) {
        CheckpointDayDTO checkpointDayDTO = new CheckpointDayDTO();
        checkpointDayDTO.setId(checkpointDay.getId());
        checkpointDayDTO.setType("CheckpointDay");
        checkpointDayDTO.setName(checkpointDay.getName());
        checkpointDayDTO.setDate(checkpointDay.getDate());  // Imposta la data
        List<Question> questions = checkpointDay.getQuestions();
        if (questions != null) {
            checkpointDayDTO.setQuestions(questions.stream()
                    .map(question -> new QuestionDTO(question.getId(), question.getQuestionText(), question.getQuestionType()))
                    .collect(Collectors.toList()));
        } else {
            checkpointDayDTO.setQuestions(Collections.emptyList());
        }

        return checkpointDayDTO;
    }

    private DeadlineDayDTO mapToDeadlineDayDTO(DeadlineDay deadlineDay) {
        DeadlineDayDTO deadlineDayDTO = new DeadlineDayDTO();
        deadlineDayDTO.setId(deadlineDay.getId());
        deadlineDayDTO.setType("DeadlineDay");
        deadlineDayDTO.setName(deadlineDay.getName());
        deadlineDayDTO.setDate(deadlineDay.getDate());  // Imposta la data
        List<Question> questions = deadlineDay.getQuestions();
        if (questions != null) {
            deadlineDayDTO.setQuestions(questions.stream()
                    .map(question -> new QuestionDTO(question.getId(), question.getQuestionText(), question.getQuestionType()))
                    .collect(Collectors.toList()));
        } else {
            deadlineDayDTO.setQuestions(Collections.emptyList());
        }

        return deadlineDayDTO;
    }

}
