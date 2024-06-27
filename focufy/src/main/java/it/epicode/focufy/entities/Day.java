package it.epicode.focufy.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name="Days")
public abstract class Day {
   @Id
   @GeneratedValue
   private int id;

   private int availableHours;

   @ManyToOne
   @JoinColumn(name = "studyplan_id")
   @JsonIgnore
   private StudyPlan studyPlan;

   public abstract String getType();

}
