package it.epicode.focufy.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class StudyDay extends Day {

    private String type = "StudyDay";

    @OneToMany(mappedBy = "studyDay")
    @JsonIgnore
    private List<ActivitySession> activitySessions = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "mantra_id")
    @JsonIgnore
    private Mantra mantra;

}
