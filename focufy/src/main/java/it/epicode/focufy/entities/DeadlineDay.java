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
public class DeadlineDay extends Day{

    private String type = "DeadlineDay";

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "deadline_day_question",
            joinColumns = @JoinColumn(name = "deadline_day_id"),
            inverseJoinColumns = @JoinColumn(name = "question_id")
    )
    @JsonIgnore
    private List<Question> questions = new ArrayList<>();

    @OneToMany(mappedBy = "deadlineDay", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DeadlineAnswer> deadlineAnswers = new ArrayList<>();
}
