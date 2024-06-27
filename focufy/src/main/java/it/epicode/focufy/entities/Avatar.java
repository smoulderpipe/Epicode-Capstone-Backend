package it.epicode.focufy.entities;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name="avatars")
@NoArgsConstructor
public class Avatar {

    @Id
    @GeneratedValue
    private int id;

    @ManyToOne
    @JoinColumn(name = "chronotype_id")
    private Chronotype chronotype;

    @ManyToOne
    @JoinColumn(name = "temper_id")
    private Temper temper;

    private String image;

    @ManyToMany(mappedBy = "avatar")
    private Set<User> users = new HashSet<>();

    public Avatar(Chronotype chronotype, Temper temper, String image) {
        this.chronotype = chronotype;
        this.temper = temper;
        this.image = image;
    }
}
