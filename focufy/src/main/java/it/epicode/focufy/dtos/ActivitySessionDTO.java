package it.epicode.focufy.dtos;

import it.epicode.focufy.entities.enums.ActivitySessionType;
import lombok.Data;

import java.time.LocalTime;

@Data
public class ActivitySessionDTO {

    private ActivitySessionType activitySessionType;

    private int duration;
    private LocalTime startTime;
    public ActivitySessionDTO(ActivitySessionType activitySessionType, int duration, LocalTime startTime) {
        this.activitySessionType = activitySessionType;
        this.duration = duration;
        this.startTime = startTime;
    }

    public ActivitySessionDTO() {
    }

}
