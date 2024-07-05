package it.epicode.focufy.entities;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name="Days")
public abstract class Day {
   @Id
   @GeneratedValue
   private int id;

   private String name;

   private int availableHours;

   @ManyToOne
   @JoinColumn(name = "studyplan_id")
   @JsonIgnore
   private StudyPlan studyPlan;

   private LocalDate date;

   private static int nameCounter = 1;

   public Day() {
      this.name = "DAY " + nameCounter;
      nameCounter++;
   }

   public abstract String getType();
}
