package it.epicode.focufy.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.epicode.focufy.entities.enums.AnswerType;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name="answers")
public class Answer {

    @Id
    @GeneratedValue
    private int id;

    @ManyToOne
    @JoinColumn(name="user_id")
    @JsonIgnore
    private User user;

    @ManyToOne
    @JoinColumn(name="question_id")
    @JsonIgnore
    private Question question;

    @Enumerated(EnumType.STRING)
    private AnswerType answerType;

    private String answerText;

    private Integer numericAnswer;

    private boolean isMultipleChoice;
}
