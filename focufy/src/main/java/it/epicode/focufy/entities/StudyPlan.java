package it.epicode.focufy.entities;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.util.List;
import java.util.Objects;

@Data
@Entity
@Table(name="study_plans")
public class StudyPlan {

    @Id
    @GeneratedValue
    private int id;

    @OneToOne
    @JsonBackReference
    private User user;

    @Column(name="short_term_goal")
    private String shortTermGoal;

    @OneToMany(mappedBy = "studyPlan", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Day> days;

    @ManyToMany
    @JoinTable(
            name = "study_plan_mantra",
            joinColumns = @JoinColumn(name = "study_plan_id"),
            inverseJoinColumns = @JoinColumn(name = "mantra_id")
    )
    @JsonIgnore
    private List<Mantra> mantras;

    @Override
    public int hashCode() {
        return Objects.hash(id, shortTermGoal, mantras);
    }


}
