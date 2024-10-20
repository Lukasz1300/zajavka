package projektZajavka2.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "menu_items")
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    @NotNull(message = "Restauracja nie może być pusta")
    private Restaurant restaurant;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "Nazwa pozycji menu jest wymagana")
    @Size(max = 100, message = "Nazwa pozycji menu nie może przekraczać 100 znaków")
    private String name;

    @Column(columnDefinition = "TEXT")
    @Size(max = 2000, message = "Opis nie może przekraczać 2000 znaków")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Cena jest wymagana")
    @Positive(message = "Cena musi być większa niż zero")
    private BigDecimal price;

    @Column(name = "image_url", length = 255)
    @Size(max = 255, message = "Adres URL obrazu nie może przekraczać 255 znaków")
    private String imageUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToMany
    @JoinTable(
            name = "menu_item_categories",
            joinColumns = @JoinColumn(name = "menu_item_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories = new HashSet<>();

    @OneToMany(mappedBy = "menuItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderItem> orderItems = new HashSet<>();

    // Metoda do dodawania kategorii
    public void addCategory(Category category) {
        categories.add(category);
        category.getMenuItems().add(this);
    }

    // Metoda do usuwania kategorii
    public void removeCategory(Category category) {
        categories.remove(category);
        category.getMenuItems().remove(this);
    }

    @Override
    public String toString() {
        return "MenuItem{" +
                "id=" + id +
                ", restaurant=" + restaurant +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", imageUrl='" + imageUrl + '\'' +
                ", createdAt=" + createdAt +
                ", categories=" + categories +
                ", orderItems=" + orderItems +
                '}';
    }
}
