package it.epicode.focufy.services;

import it.epicode.focufy.dtos.StudyPlanDTO;
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
        for (int i = 0; i < studyPlanDTO.getNumberOfDays(); i++) {
            Day day;
            if ((i + 1) % 7 == 0) {
                day = new CheckpointDay();
                ((CheckpointDay) day).setStudyPlan(studyPlan);
                days.add(day);
            } else {
                StudyDay studyDay = new StudyDay();
                studyDay.setStudyPlan(studyPlan);
                addActivitySessionsToStudyDay(studyDay, user.getAvatar().getChronotype().getChronotypeType());
                days.add(studyDay);
            }
        }
        studyPlan.setDays(days);

        // Salva il piano di studi e associa l'utente
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

        studyPlan.getMantras().addAll(mantras);
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

    public void deleteStudyPlanByUserId(int userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found."));
        StudyPlan studyPlan = user.getStudyPlan();
        if (studyPlan != null) {
            // Disassocia il piano di studi dall'utente
            user.setStudyPlan(null);
            userRepo.save(user);

            // Rimuovi le entità dipendenti
            List<Day> days = studyPlan.getDays();

            for (Day day : days) {
                if (day instanceof StudyDay) {
                    StudyDay studyDay = (StudyDay) day;
                    for (ActivitySession session : studyDay.getActivitySessions()) {
                        activitySessionRepo.delete(session);
                    }
                    studyDayRepo.delete(studyDay);
                } else if (day instanceof CheckpointDay) {
                    CheckpointDay checkpointDay = (CheckpointDay) day;
                    checkpointDayRepo.delete(checkpointDay);
                }
            }

            // Elimina il piano di studi
            studyPlanRepo.delete(studyPlan);
        } else {
            throw new NotFoundException("StudyPlan not found for user with id=" + userId);
        }
    }
}
