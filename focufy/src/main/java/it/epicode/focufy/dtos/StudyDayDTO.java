package it.epicode.focufy.dtos;

import it.epicode.focufy.entities.StudyDay;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class StudyDayDTO extends DayDTO {

    private String mantra;
    private List<ActivitySessionDTO> activitySessions;

    public StudyDayDTO(StudyDay studyDay) {
        super(studyDay.getId(), "StudyDay", studyDay.getName());
        this.mantra = studyDay.getMantra() != null ? studyDay.getMantra().getText() : null;
        this.activitySessions = studyDay.getActivitySessions().stream()
                .map(session -> {
                    ActivitySessionDTO dto = new ActivitySessionDTO();
                    dto.setActivitySessionType(session.getActivitySessionType());
                    dto.setDuration(session.getDuration());
                    dto.setStartTime(session.getStartTime());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public StudyDayDTO() {
        super();
    }
}