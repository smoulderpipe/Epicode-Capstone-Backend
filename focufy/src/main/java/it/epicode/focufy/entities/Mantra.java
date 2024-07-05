package it.epicode.focufy.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.epicode.focufy.entities.enums.MantraType;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "mantras")
public class Mantra {

    @Id
    @GeneratedValue
    private int id;

    private String text;

    @Enumerated(EnumType.STRING)
    private MantraType mantraType;

    @ManyToMany(mappedBy = "mantras")
    @JsonIgnore
    private List<StudyPlan> studyPlans;

}
