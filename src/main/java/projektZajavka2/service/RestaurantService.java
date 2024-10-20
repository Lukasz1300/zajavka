package projektZajavka2.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import projektZajavka2.entity.Restaurant;
import projektZajavka2.repository.RestaurantRepository;

import java.util.List;
import java.util.Optional;

@Transactional
@Service
@Slf4j
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    // wsztrzykniecie zalerznosci przez konstruktor mozna dodac adnotacje @AllArgsConstructor
    @Autowired
    public RestaurantService(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    public List<Restaurant> findAllRestaurants() {
        return restaurantRepository.findAll();
    }

    public Optional<Restaurant> findRestaurantById(Long id) {
        return restaurantRepository.findById(id);
    }

    public List<Restaurant> findRestaurantsByOwnerId(Long ownerId) {
        return restaurantRepository.findByOwnerId(ownerId);
    }

    public Restaurant saveRestaurant(Restaurant restaurant) {
        return restaurantRepository.save(restaurant);
    }

    public void deleteRestaurantById(Long id) {
        restaurantRepository.deleteById(id);
    }
}


