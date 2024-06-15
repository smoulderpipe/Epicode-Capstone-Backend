package it.epicode.focufy.entities;
import it.epicode.focufy.entities.enums.ChronotypeType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name="chronotypes")
@NoArgsConstructor
@AllArgsConstructor
public class Chronotype {
    @Id
    @GeneratedValue
    private int id;

    @Enumerated(EnumType.STRING)
    private ChronotypeType chronotypeType;

    private String description;

    public Chronotype(ChronotypeType chronotypeType) {
        this.chronotypeType = chronotypeType;
    }

}
