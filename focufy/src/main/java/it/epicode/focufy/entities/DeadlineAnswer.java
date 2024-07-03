package it.epicode.focufy.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.epicode.focufy.entities.enums.CDAnswerType;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class DeadlineAnswer extends Answer{

    @Id
    @GeneratedValue
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deadline_day_id")
    @JsonIgnore
    private DeadlineDay deadlineDay;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @Enumerated
    private CDAnswerType answerType;
}
