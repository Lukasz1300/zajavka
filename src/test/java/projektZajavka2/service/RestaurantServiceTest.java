package projektZajavka2.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import projektZajavka2.entity.Restaurant;
import projektZajavka2.repository.RestaurantRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class RestaurantServiceTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private RestaurantService restaurantService;

    @Test
    public void shouldFindAllRestaurants() {
        // Given
        List<Restaurant> expectedRestaurants = Collections.singletonList(new Restaurant());
        when(restaurantRepository.findAll()).thenReturn(expectedRestaurants);

        // When
        List<Restaurant> actualRestaurants = restaurantService.findAllRestaurants();

        // Then
        assertEquals(expectedRestaurants, actualRestaurants);
    }

    @Test
    public void shouldFindRestaurantById() {
        // Given
        Long id = 1L;
        Restaurant expectedRestaurant = new Restaurant();
        when(restaurantRepository.findById(id)).thenReturn(Optional.of(expectedRestaurant));

        // When
        Optional<Restaurant> actualRestaurant = restaurantService.findRestaurantById(id);

        // Then
        assertTrue(actualRestaurant.isPresent());
        assertEquals(expectedRestaurant, actualRestaurant.get());
    }

    @Test
    public void shouldFindRestaurantsByOwnerId() {
        // Given
        Long ownerId = 1L;
        List<Restaurant> expectedRestaurants = Collections.singletonList(new Restaurant());
        when(restaurantRepository.findByOwnerId(ownerId)).thenReturn(expectedRestaurants);

        // When
        List<Restaurant> actualRestaurants = restaurantService.findRestaurantsByOwnerId(ownerId);

        // Then
        assertEquals(expectedRestaurants, actualRestaurants);
    }

    @Test
    public void shouldSaveRestaurant() {
        // Given
        Restaurant restaurant = new Restaurant();
        when(restaurantRepository.save(restaurant)).thenReturn(restaurant);

        // When
        Restaurant savedRestaurant = restaurantService.saveRestaurant(restaurant);

        // Then
        assertEquals(restaurant, savedRestaurant);
    }

    @Test
    public void shouldDeleteRestaurantById() {
        // Given
        Long id = 1L;
        doNothing().when(restaurantRepository).deleteById(id);

        // When
        restaurantService.deleteRestaurantById(id);

        // Then
        verify(restaurantRepository, times(1)).deleteById(id);
    }
}
