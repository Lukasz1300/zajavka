package projektZajavka2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import projektZajavka2.entity.OrderItem;

import java.util.List;


public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // Znajdź wszystkie pozycje zamówienia na podstawie ID zamówienia
    List<OrderItem> findByOrderId(Long orderId);

    // Znajdź wszystkie pozycje zamówienia na podstawie ID pozycji menu
    List<OrderItem> findByMenuItemId(Long menuItemId);
}
