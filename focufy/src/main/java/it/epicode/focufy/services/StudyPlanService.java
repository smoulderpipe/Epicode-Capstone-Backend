package it.epicode.focufy.services;

import it.epicode.focufy.dtos.StudyPlanDTO;
import it.epicode.focufy.dtos.StudyPlanResponseDTO;
import it.epicode.focufy.entities.*;
import it.epicode.focufy.entities.enums.ActivitySessionType;
import it.epicode.focufy.entities.enums.ChronotypeType;
import it.epicode.focufy.entities.enums.MantraType;
import it.epicode.focufy.entities.enums.RiskType;
import it.epicode.focufy.exceptions.NotFoundException;
import it.epicode.focufy.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

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
    private ActivitySessionRepo activitySessionRepo;

    @Autowired
    private ActivitySessionService activitySessionService;

    @Transactional
    public StudyPlan saveStudyPlanAndCreateDays(StudyPlanDTO studyPlanDTO) {
        User user = userRepo.findById(studyPlanDTO.getUserId())
                .orElseThrow(() -> new NotFoundException("User with id=" + studyPlanDTO.getUserId() + " not found."));

        StudyPlan studyPlan = new StudyPlan();
        studyPlan.setUser(user);
        studyPlan.setShortTermGoal(studyPlanDTO.getShortTermGoal());

        List<Day> days = new ArrayList<>();
        int numberOfDays = studyPlanDTO.getNumberOfDays();
        for (int i = 0; i < numberOfDays; i++) {
            Day day;
            if (i == numberOfDays - 1) {
                DeadlineDay deadlineDay = new DeadlineDay();
                deadlineDay.setStudyPlan(studyPlan);
                days.add(deadlineDay);
            } else if ((i + 1) % 7 == 0) {
                CheckpointDay checkpointDay = new CheckpointDay();
                checkpointDay.setStudyPlan(studyPlan);
                days.add(checkpointDay);
            } else {
                StudyDay studyDay = new StudyDay();
                studyDay.setStudyPlan(studyPlan);
                addActivitySessionsToStudyDay(studyDay, user.getAvatar().getChronotype().getChronotypeType());
                days.add(studyDay);
            }
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
            case LION -> LocalTime.of(17, 0); // 17:00 (5:00 di pomeriggio)
            case BEAR -> LocalTime.of(19, 0); // 19:00 (7:00 di sera)
            case DOLPHIN -> LocalTime.of(20, 0); // 20:00 (8:00 di sera)
            case WOLF -> LocalTime.of(11, 0); // 11:00 del mattino
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
                    studyDay.setMantra(null); // Rimuovi il mantra da StudyDay

                    for (ActivitySession session : studyDay.getActivitySessions()) {
                        activitySessionRepo.delete(session); // Cancella tutte le sessioni associate a StudyDay
                    }
                    studyDayRepo.delete(studyDay); // Cancella lo StudyDay dal repository
                } else if (day instanceof CheckpointDay) {
                    CheckpointDay checkpointDay = (CheckpointDay) day;
                    checkpointDayRepo.delete(checkpointDay); // Cancella il CheckpointDay dal repository
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

        return new StudyPlanResponseDTO(studyPlan);
    }

}
