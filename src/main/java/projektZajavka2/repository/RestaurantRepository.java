package projektZajavka2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import projektZajavka2.entity.Restaurant;

import java.util.List;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    List<Restaurant> findByOwnerId(Long ownerId);

   }
