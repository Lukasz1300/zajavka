package projektZajavka2.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import projektZajavka2.entity.Restaurant;
import projektZajavka2.service.RestaurantService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantRestController {

    private final RestaurantService restaurantService;

    @Autowired
    public RestaurantRestController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    // Pobranie wszystkich restauracji
    // GET /api/restaurants
    @GetMapping
    public ResponseEntity<List<Restaurant>> getAllRestaurants() {
        List<Restaurant> restaurants = restaurantService.findAllRestaurants();
        return new ResponseEntity<>(restaurants, HttpStatus.OK);
    }

    // Pobranie restauracji według ID
    @GetMapping("/{id}")
    public ResponseEntity<Restaurant> getRestaurantById(@PathVariable Long id) {
        Optional<Restaurant> restaurant = restaurantService.findRestaurantById(id);
        return restaurant.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Pobranie restauracji według ID właściciela
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<Restaurant>> getRestaurantsByOwnerId(@PathVariable Long ownerId) {
        List<Restaurant> restaurants = restaurantService.findRestaurantsByOwnerId(ownerId);
        return new ResponseEntity<>(restaurants, HttpStatus.OK);
    }

    // Dodanie nowej restauracji
    @PostMapping
    public ResponseEntity<Restaurant> createRestaurant(@RequestBody Restaurant restaurant) {
        Restaurant savedRestaurant = restaurantService.saveRestaurant(restaurant);
        return new ResponseEntity<>(savedRestaurant, HttpStatus.CREATED);
    }

    // Aktualizacja restauracji
    @PutMapping("/{id}")
    public ResponseEntity<Restaurant> updateRestaurant(@PathVariable Long id, @RequestBody Restaurant restaurant) {
        Optional<Restaurant> existingRestaurant = restaurantService.findRestaurantById(id);
        if (existingRestaurant.isPresent()) {
            restaurant.setId(id);
            Restaurant updatedRestaurant = restaurantService.saveRestaurant(restaurant);
            return new ResponseEntity<>(updatedRestaurant, HttpStatus.OK);
        }
        return ResponseEntity.notFound().build();
    }

    // Usuwanie restauracji według ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable Long id) {
        Optional<Restaurant> existingRestaurant = restaurantService.findRestaurantById(id);
        if (existingRestaurant.isPresent()) {
            restaurantService.deleteRestaurantById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
