package it.epicode.focufy.entities;
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

    public Temper(TemperType temperType) {
        this.temperType = temperType;
    }
}
