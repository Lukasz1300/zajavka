package projektZajavka2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import projektZajavka2.entity.DeliveryArea;

import java.util.List;


public interface DeliveryAreaRepository extends JpaRepository<DeliveryArea, Long > {

    // Metoda do znalezienia obszarów dostawy według identyfikatora restauracji
    List<DeliveryArea> findByRestaurantId(Long restaurantId);

}
