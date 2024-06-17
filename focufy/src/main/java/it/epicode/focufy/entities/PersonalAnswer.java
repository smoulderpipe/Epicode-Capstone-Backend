package it.epicode.focufy.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(name="personal_answers")
@Entity
public class PersonalAnswer extends Answer {

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    private int timeDays;
    private String shortTermGoal;
    private String longTermGoal;
    private int satisfaction;
    private boolean restart;
}
