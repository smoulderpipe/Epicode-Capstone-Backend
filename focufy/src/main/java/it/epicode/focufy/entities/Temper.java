package it.epicode.focufy.entities;
import it.epicode.focufy.entities.enums.RiskType;
import it.epicode.focufy.entities.enums.StrengthType;
import it.epicode.focufy.entities.enums.TemperType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name="tempers")
@NoArgsConstructor
@AllArgsConstructor
public class Temper {
    @Id
    @GeneratedValue
    private int id;

    @Enumerated(EnumType.STRING)
    private TemperType temperType;

    private String description;

    @Enumerated(EnumType.STRING)
    private StrengthType strengthType;

    @Enumerated(EnumType.STRING)
    private RiskType riskType;

    public Temper(TemperType temperType) {
        setTemperType(temperType);
    }

    public void setTemperType(TemperType temperType) {
        this.temperType = temperType;
        switch (temperType) {
            case WHIMSICAL:
                this.strengthType = StrengthType.ACUMEN;
                this.riskType = RiskType.DISTRACTION;
                this.description = "their unpredictable nature keeps things fresh, with bursts of creative energy fueling exploration. Their insightful way of thinking can lead them to places no one else can reach, but focus can be fleeting for them.";
                break;
            case TENACIOUS:
                this.strengthType = StrengthType.DISCIPLINE;
                this.riskType = RiskType.BOREDOM;
                this.description = "they methodically explore, conquering challenges with unwavering focus. Their desire for measurable achievements guarantees progress, but the risk of monotony lurks.";
                break;
            case TACTICAL:
                this.strengthType = StrengthType.FORESIGHT;
                this.riskType = RiskType.OVERTHINKING;
                this.description = "their foresight is their guiding light, enabling them to navigate complex challenges with precision. However, overthinking can lead to inaction, hindering their progress.";
                break;
            case GREEDY:
                this.strengthType = StrengthType.PASSION;
                this.riskType = RiskType.BURNOUT;
                this.description = "they are relentless in their pursuit of knowledge, as their curiosity drives them to building playgrounds for exploration. Passion fuels their growth, but risks burnout if not balanced with well-being.";
                break;
            default:
                throw new IllegalArgumentException("Unknown temper type: " + temperType);
        }
    }
}
