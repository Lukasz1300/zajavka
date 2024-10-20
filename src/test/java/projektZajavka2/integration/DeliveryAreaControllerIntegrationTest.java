package projektZajavka2.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import projektZajavka2.entity.DeliveryArea;
import projektZajavka2.entity.Restaurant;
import projektZajavka2.repository.DeliveryAreaRepository;
import projektZajavka2.repository.RestaurantRepository;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class DeliveryAreaControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DeliveryAreaRepository deliveryAreaRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    private Restaurant restaurant;

    private DeliveryArea deliveryArea;

    @BeforeEach
    public void setUp() {

        // Tworzenie restauracji  dodanie zmiennej: private Restaurant restaurant
        restaurant = new Restaurant();
        restaurant.setName("Test Restaurant");
        restaurant.setAddress("Test Restaurant Address");
        restaurant.setPhoneNumber("+123456789");
        restaurant.setEmail("restaurant@example.com");
        restaurant.setCreatedAt(LocalDateTime.now());
        restaurant = restaurantRepository.save(restaurant); // Przypisanie do pola klasy
        System.out.println(restaurant);

        // Tworzenie obiektu DeliveryArea
        deliveryArea = new DeliveryArea(); // Użyj this.deliveryArea
        deliveryArea.setRestaurant(restaurant); // Ustawienie restauracji
        deliveryArea.setStreetName("Main Street"); // Ustawienie nazwy ulicy
        this.deliveryArea = deliveryAreaRepository.save(deliveryArea);
        System.out.println(this.deliveryArea);
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    public void testGetDeliveryAreasByRestaurantId() throws Exception {
        Restaurant restaurant = restaurantRepository.findAll().get(0);

        mockMvc.perform(get("/deliveryareas/restaurant/{restaurantId}", restaurant.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("/deliveryareas/list"))
                .andExpect(model().attributeExists("deliveryAreas"))
                .andExpect(model().attribute("deliveryAreas", hasSize(1)))
                .andExpect(model().attribute("deliveryAreas", hasItem(
                        hasProperty("streetName", is("Main Street")) // Zmiana tutaj
                )));
    }
}
