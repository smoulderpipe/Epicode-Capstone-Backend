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

   private String name;

   private int availableHours;


   private static int progressiveId = 1;

   @ManyToOne
   @JoinColumn(name = "studyplan_id")
   @JsonIgnore
   private StudyPlan studyPlan;

   public Day() {
      this.name = getClass().getSimpleName() + "_" + progressiveId;
      progressiveId++;
   }

   public abstract String getType();

}
