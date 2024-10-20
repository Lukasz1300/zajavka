package projektZajavka2.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "delivery_areas")
public class DeliveryArea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    @NotNull(message = "Restauracja nie może być pusta")
    private Restaurant restaurant;

    @Column(name = "street_name", length = 255)
    @NotBlank(message = "Nazwa ulicy jest wymagana")
    @Size(max = 255, message = "Nazwa ulicy nie może przekraczać 255 znaków") //  długość nie przekracza 255 znaków
    private String streetName;

}
