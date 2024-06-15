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
                break;
            case TENACIOUS:
                this.strengthType = StrengthType.DISCIPLINE;
                this.riskType = RiskType.BOREDOM;
                break;
            case TACTICAL:
                this.strengthType = StrengthType.FORESIGHT;
                this.riskType = RiskType.OVERTHINKING;
                break;
            case GREEDY:
                this.strengthType = StrengthType.PASSION;
                this.riskType = RiskType.BURNOUT;
                break;
            default:
                throw new IllegalArgumentException("Unknown temper type: " + temperType);
        }
    }
}
