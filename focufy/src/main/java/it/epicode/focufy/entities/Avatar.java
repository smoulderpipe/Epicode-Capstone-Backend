package it.epicode.focufy.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name="avatars")
public class Avatar {

    @Id
    @GeneratedValue
    private int id;

    @OneToOne
    private Chronotype chronotype;

    @OneToOne
    private Temper temper;

    private String image;
    private String description;

    @OneToOne
    private User user;
}
