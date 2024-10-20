package projektZajavka2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import projektZajavka2.entity.MenuItem;

import java.util.List;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    // Niestandardowa metoda do znalezienia pozycji menu według identyfikatora restauracji
    List<MenuItem> findByRestaurantId(Long restaurantId);
}
