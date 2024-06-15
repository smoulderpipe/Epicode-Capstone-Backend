package it.epicode.focufy.repositories;
import it.epicode.focufy.entities.Avatar;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvatarRepo extends JpaRepository<Avatar, Integer> {
}
