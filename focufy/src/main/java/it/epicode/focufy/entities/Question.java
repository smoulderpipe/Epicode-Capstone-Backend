package it.epicode.focufy.entities;
import com.fasterxml.jackson.annotation.JsonIgnore;
import it.epicode.focufy.entities.enums.QuestionType;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name="questions")
public class Question {
    @Id
    @GeneratedValue
    private int id;
    private String questionText;

    @Enumerated(EnumType.STRING)
    private QuestionType questionType;

    @OneToMany(mappedBy = "question")
    @JsonIgnore
    List<Answer> answers;

    @ManyToMany(mappedBy = "questions")
    @JsonIgnore
    private List<DeadlineDay> deadlineDays;

    @ManyToMany(mappedBy = "questions")
    @JsonIgnore
    private List<CheckpointDay> checkpointDays;
}
