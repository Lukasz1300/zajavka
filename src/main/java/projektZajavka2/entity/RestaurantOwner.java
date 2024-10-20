package projektZajavka2.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;


import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@AllArgsConstructor
@Table(name = "restaurant_owners")
public class RestaurantOwner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nazwa właściciela jest wymagana")
    @Size(max = 100, message = "Nazwa właściciela nie może przekraczać 100 znaków")
    @Column(nullable = false, length = 100)
    private String name;

    @NotBlank(message = "Email jest wymagany")
    @Email(message = "Email powinien być prawidłowy")
    @Size(max = 100, message = "Email nie może przekraczać 100 znaków")
    @Column(nullable = false, length = 100)
    private String email;

    @Pattern(regexp = "^\\+?[0-9]{1,15}$", message = "Numer telefonu musi zawierać tylko cyfry i opcjonalny znak '+' na początku, oraz mieć maksymalnie 15 cyfr")
    @Column(name = "phone_number", length = 15)
    private String phoneNumber;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Restaurant> restaurants = new HashSet<>();

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public RestaurantOwner() {
        this.createdAt = LocalDateTime.now(); // Ustawienie domyślnej wartości
    }
}
