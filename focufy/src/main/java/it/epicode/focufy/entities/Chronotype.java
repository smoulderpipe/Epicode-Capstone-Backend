package it.epicode.focufy.entities;
import it.epicode.focufy.entities.enums.ChronotypeType;
import it.epicode.focufy.entities.enums.MaxEnergyType;
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
                this.description = " thrive on infectious enthusiasm and solid optimism. They tackle each day with unwavering self-belief, ready to conquer their studies.";
                break;
            case BEAR:
                this.maxEnergyType = MaxEnergyType.AFTERNOON;
                this.description = " are known for their measured approach to life, and exude a sense of charismatic composure. This stems from their ability to face life's tempests with quiet control.";
                break;
            case DOLPHIN:
                this.maxEnergyType = MaxEnergyType.EVENING;
                this.description = " are renowned for their alertness, constantly buzzing with stored energy. This vigilance fuels their problem solving energy tank.";
                break;
            case WOLF:
                this.maxEnergyType = MaxEnergyType.NIGHT;
                this.description = " find focus and inspiration in the quiet hours of the night, as the world around them stills, undisturbed by the distractions of excessive company. ";
                break;
            default:
                throw new IllegalArgumentException("Unknown chronotype type: " + chronotypeType);
        }
    }

}
