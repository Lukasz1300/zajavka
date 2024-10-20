package projektZajavka2.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "User cannot be null")
    @JsonIgnore // Zignoruj odniesienie do user podczas serializacji
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    @NotNull(message = "Restaurant cannot be null")
    private Restaurant restaurant;

    @Column(name = "order_status", length = 50)
    @NotBlank(message = "Order status cannot be blank")
    @Size(max = 50, message = "Order status cannot exceed 50 characters")
    private String orderStatus = "PENDING";

    @Column(name = "total_price", precision = 10, scale = 2)
    @NotNull(message = "Total price cannot be null")
    @Positive(message = "Total price must be positive")
    private BigDecimal totalPrice;

    @Column(name = "order_date")
    @NotNull(message = "Order date cannot be null")
    @PastOrPresent(message = "Order date cannot be in the future")
    private LocalDateTime orderDate = LocalDateTime.now();

    @Column(name = "delivery_address", length = 255)
    @NotBlank(message = "Delivery address cannot be blank")
    @Size(max = 255, message = "Delivery address cannot exceed 255 characters")
    private String deliveryAddress;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderItem> orderItems = new HashSet<>();

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void removeOrderItem(OrderItem orderItem) {
        orderItems.remove(orderItem);
        orderItem.setOrder(null);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", user=" + (user != null ? user.getUsername() : null) +
                ", restaurant=" + (restaurant != null ? restaurant.getName() : null) +
                ", orderStatus='" + orderStatus + '\'' +
                ", totalPrice=" + totalPrice +
                ", orderDate=" + orderDate +
                ", deliveryAddress='" + deliveryAddress + '\'' +
                ", orderItems=" + orderItems.stream().map(OrderItem::getId).collect(Collectors.toList()) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return id != null && id.equals(order.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
