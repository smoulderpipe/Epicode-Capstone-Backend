package it.epicode.focufy.entities;
import it.epicode.focufy.entities.enums.ChronotypeType;
import it.epicode.focufy.entities.enums.MaxEnergyType;
import it.epicode.focufy.entities.enums.RiskType;
import it.epicode.focufy.entities.enums.StrengthType;
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

    @Enumerated(EnumType.STRING)
    private MaxEnergyType maxEnergyType;

    private String description;

    public Chronotype(ChronotypeType chronotypeType) {
        this.chronotypeType = chronotypeType;
        switch (chronotypeType) {
            case LION:
                this.maxEnergyType = MaxEnergyType.MORNING;
                break;
            case BEAR:
                this.maxEnergyType = MaxEnergyType.AFTERNOON;
                break;
            case DOLPHIN:
                this.maxEnergyType = MaxEnergyType.EVENING;
                break;
            case WOLF:
                this.maxEnergyType = MaxEnergyType.NIGHT;
                break;
            default:
                throw new IllegalArgumentException("Unknown chronotype type: " + chronotypeType);
        }
    }

}
