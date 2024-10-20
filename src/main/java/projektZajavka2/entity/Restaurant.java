package projektZajavka2.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "restaurants")
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "owner_id", nullable = true) // Właściciel jest opcjonalny
    private RestaurantOwner owner;

    @NotBlank(message = "Nazwa jest wymagana")
    @Size(max = 100, message = "Nazwa nie może przekraczać 100 znaków")
    @Column(nullable = false, length = 100)
    private String name;

    @NotBlank(message = "Adres jest wymagany")
    @Size(max = 255, message = "Adres nie może przekraczać 255 znaków")
    @Column(nullable = false, length = 255)
    private String address;

    @Pattern(regexp = "^\\+?[0-9]{1,15}$", message = "Numer telefonu musi zawierać tylko cyfry i opcjonalny znak '+' na początku, oraz mieć maksymalnie 15 cyfr")
    @Column(name = "phone_number", length = 15)
    private String phoneNumber;

    @Email(message = "Email powinien być prawidłowy")
    @Size(max = 100, message = "Email nie może przekraczać 100 znaków")
    @Column(length = 100)
    private String email;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MenuItem> menuItems = new HashSet<>();
}
