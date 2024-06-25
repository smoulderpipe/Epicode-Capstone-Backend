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
                this.description = " thrives on infectious enthusiasm and solid optimism. They tackle each day with unwavering self-belief, ready to conquer their studies.";
                break;
            case BEAR:
                this.maxEnergyType = MaxEnergyType.AFTERNOON;
                this.description = " possess an undeniable aura that draws respectful attention, captivating others with their charisma and their magnetic presence.";
                break;
            case DOLPHIN:
                this.maxEnergyType = MaxEnergyType.EVENING;
                this.description = " is renowned for its alertness, constantly buzzing with stored energy. This vigilance fuels their problem solving energy tank.";
                break;
            case WOLF:
                this.maxEnergyType = MaxEnergyType.NIGHT;
                this.description = " finds focus and inspiration in the quiet hours of the night, as the world around them stills, undisturbed by the distractions of excessive company. ";
                break;
            default:
                throw new IllegalArgumentException("Unknown chronotype type: " + chronotypeType);
        }
    }

}
