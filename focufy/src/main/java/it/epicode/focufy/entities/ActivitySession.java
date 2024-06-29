package it.epicode.focufy.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.epicode.focufy.entities.enums.ActivitySessionType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalTime;

@Data
@Entity
@Table(name = "sessions")
public class ActivitySession {

    @Id
    @GeneratedValue
    private int id;

    @Enumerated(EnumType.STRING)
    private ActivitySessionType activitySessionType;

    private int duration;

    private LocalTime startTime;

    @ManyToOne
    @JoinColumn(name = "study_day_id")
    @JsonIgnore
    private StudyDay studyDay;
}
