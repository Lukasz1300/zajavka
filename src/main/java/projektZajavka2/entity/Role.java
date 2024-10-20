package projektZajavka2.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "roles")
public class Role implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @JsonIgnore // Ignorowanie dla serializacji
    @ManyToMany(mappedBy = "roles") // Ustawienie odwrotnego powiązania
    private Set<User> users = new HashSet<>();

    @Override
    public String getAuthority() {
        return name;
    }
}

