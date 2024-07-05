package it.epicode.focufy.repositories;
import it.epicode.focufy.entities.Mantra;
import it.epicode.focufy.entities.enums.MantraType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MantraRepo extends JpaRepository<Mantra, Integer> {
    List<Mantra> findByMantraType(MantraType mantraType);
}
