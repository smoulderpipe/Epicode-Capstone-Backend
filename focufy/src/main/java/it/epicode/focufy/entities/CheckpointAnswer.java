package it.epicode.focufy.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.epicode.focufy.entities.enums.CDAnswerType;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class CheckpointAnswer extends Answer{
    @Id
    @GeneratedValue
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checkpoint_day_id")
    @JsonIgnore
    private CheckpointDay checkpointDay;

    @Enumerated
    private CDAnswerType answerType;

}
