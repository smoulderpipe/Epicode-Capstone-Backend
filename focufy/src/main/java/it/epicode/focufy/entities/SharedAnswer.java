package it.epicode.focufy.entities;
import it.epicode.focufy.entities.enums.SharedAnswerType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "shared_answers")
@Entity
public class SharedAnswer extends Answer{

    @Enumerated(EnumType.STRING)
    private SharedAnswerType sharedAnswerType;

    @ManyToMany(mappedBy = "sharedAnswers")
    private List<User> users;

    public List<User> getUsers() {
        if (users == null) {
            users = new ArrayList<>();
        }
        return users;
    }

}
