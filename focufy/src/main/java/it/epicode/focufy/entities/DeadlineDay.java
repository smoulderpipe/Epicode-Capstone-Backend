package it.epicode.focufy.entities;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class DeadlineDay extends Day{

    private String type = "DeadlineDay";

    @OneToMany
    @JsonIgnore
    private List<Question> questions = new ArrayList<>();
}
