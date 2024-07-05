package it.epicode.focufy.services;
import it.epicode.focufy.entities.ActivitySession;
import it.epicode.focufy.entities.enums.ActivitySessionType;
import it.epicode.focufy.repositories.ActivitySessionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Service
public class ActivitySessionService {

    @Autowired
    private ActivitySessionRepo activitySessionRepo;

    public ActivitySession saveActivitySession(ActivitySessionType type, int durationHours, LocalTime startTime){
        ActivitySession activitySession = new ActivitySession();
        activitySession.setStartTime(startTime);
        activitySession.setDuration(durationHours);
        activitySession.setActivitySessionType(type);
        return activitySessionRepo.save(activitySession);
    }
}
