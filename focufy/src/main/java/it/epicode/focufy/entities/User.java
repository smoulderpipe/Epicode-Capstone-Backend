package it.epicode.focufy.entities;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import it.epicode.focufy.entities.enums.UserRole;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;


@Data
@Entity
@Table(name="users")
public class User implements UserDetails {
    @Id
    @GeneratedValue
    private int id;

    private String name;
    private String email;
    private String password;

    private boolean confirmation;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role")
    private UserRole userRole;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<PersonalAnswer> personalAnswers = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<CheckpointAnswer> checkpointAnswers = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<DeadlineAnswer> deadlineAnswers = new ArrayList<>();

    @ManyToMany(mappedBy = "users", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<SharedAnswer> sharedAnswers = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "avatar_id")
    @JsonIgnore
    private Avatar avatar;

    @OneToOne(mappedBy = "user")
    @JsonManagedReference
    private StudyPlan studyPlan;

    private String longTermGoal;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(userRole.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, email, userRole);
    }
}
