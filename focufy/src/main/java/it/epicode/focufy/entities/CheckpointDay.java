package it.epicode.focufy.entities;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class CheckpointDay extends Day{

    private String type = "CheckpointDay";

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "checkpoint_day_question",
            joinColumns = @JoinColumn(name = "checkpoint_day_id"),
            inverseJoinColumns = @JoinColumn(name = "question_id")
    )
    @JsonIgnore
    private List<Question> questions = new ArrayList<>();

    @OneToMany(mappedBy = "checkpointDay", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CheckpointAnswer> checkpointAnswers = new ArrayList<>();

}
