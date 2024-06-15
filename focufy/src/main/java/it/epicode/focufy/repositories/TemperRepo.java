package it.epicode.focufy.repositories;
import it.epicode.focufy.entities.Temper;
import it.epicode.focufy.entities.enums.TemperType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TemperRepo extends JpaRepository<Temper, Integer> {
    Temper findByTemperType(TemperType temperType);
}
