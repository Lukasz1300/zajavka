package projektZajavka2.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import projektZajavka2.entity.Order;
import projektZajavka2.entity.Restaurant;
import projektZajavka2.entity.User;
import projektZajavka2.repository.OrderRepository;
import projektZajavka2.repository.RestaurantRepository;
import projektZajavka2.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class OrderRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private RestaurantRepository restaurantRepository;
    @Autowired
    private UserRepository userRepository;

    private Order order;
    private User user;
    private Restaurant restaurant;

    @Autowired
    private ObjectMapper objectMapper; // Dodaj to do swojego testu

    @LocalServerPort
    private int port;

    @BeforeEach
    public void setup() {
        RestAssured.port = port;

        // Tworzenie nowego użytkownika
        user = new User();
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setEmail("testuser@example.com");
        user = userRepository.save(user);

        // Tworzenie nowej restauracji
        restaurant = new Restaurant();
        restaurant.setName("Test Restaurant");
        restaurant.setAddress("Test Restaurant Address");
        restaurant.setPhoneNumber("+123456789");
        restaurant.setEmail("restaurant@example.com");
        restaurant.setCreatedAt(LocalDateTime.now());
        restaurant = restaurantRepository.save(restaurant);

        // Tworzenie nowego obiektu Order
        order = new Order();
        order.setUser(user);
        order.setRestaurant(restaurant);
        order.setOrderStatus("PENDING");
        order.setTotalPrice(new BigDecimal("19.99"));
        order.setOrderDate(LocalDateTime.now());
        order.setDeliveryAddress("Test Delivery Address");
        order = orderRepository.save(order);
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    public void testGetAllOrders() throws Exception {
        mockMvc.perform(get("/api/orders")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$", is(not(empty())))) // Sprawdza, czy lista zamówień nie jest pusta
                .andExpect(jsonPath("$[0].orderStatus", is(order.getOrderStatus()))) // Sprawdza status zamówienia
                .andExpect(jsonPath("$[0].totalPrice", is(order.getTotalPrice().doubleValue()))) // Sprawdza cenę zamówienia
                .andExpect(jsonPath("$[0].deliveryAddress", is(order.getDeliveryAddress()))); // Sprawdza adres dostawy
    }

    // Test GET /api/restaurants
    @Test
    @DirtiesContext
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    public void powinnoZwrocicWszystkieRestauracje() throws Exception {
        String restaurantName = restaurant.getName();

        mockMvc.perform(get("/api/restaurants")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1))) // Sprawdź liczbę restauracji
                .andExpect(jsonPath("$[0].name").value(restaurantName)); // Użyj zmiennej do sprawdzenia nazwy
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    public void testGetOrderById() throws Exception {
        Long orderId = order.getId(); // Pobranie ID z istniejącego zamówienia

        mockMvc.perform(get("/api/orders/{id}", orderId)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("id", is(orderId.intValue())));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    public void testGetOrdersByUserId() throws Exception {
        Long userId = user.getId(); // Pobranie ID z istniejącego użytkownika

        mockMvc.perform(get("/api/orders/user/{userId}", userId)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$", not(empty())));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    public void testGetOrdersByRestaurantId() throws Exception {
        Long restaurantId = restaurant.getId(); // Pobranie ID z istniejącej restauracji

        mockMvc.perform(get("/api/orders/restaurant/{restaurantId}", restaurantId)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$", not(empty())));
    }

    @Test
    @WithMockUser(username = "adminuser", roles = {"ADMIN"})
    public void testCreateOrder() throws Exception {
        // Utworzenie nowej restauracji (jeśli nie chcesz używać tej z setup)
        Restaurant differentRestaurant = new Restaurant();
        differentRestaurant.setName("Another Restaurant");
        differentRestaurant.setAddress("789 Another Street");
        restaurantRepository.save(differentRestaurant);

        // Tworzenie nowego użytkownika z innymi danymi
        User differentUser = new User();
        differentUser.setUsername("differentuser");
        differentUser.setPassword("differentPassword");
        differentUser.setEmail("differentuser@example.com");
        userRepository.save(differentUser);

        // Utworzenie nowego zamówienia z innym użytkownikiem i restauracją
        Order newOrder = new Order();
        newOrder.setUser(differentUser); // Użyj nowo utworzonego użytkownika
        newOrder.setRestaurant(differentRestaurant); // Użyj nowo utworzonej restauracji
        newOrder.setOrderStatus("NEW");
        newOrder.setTotalPrice(BigDecimal.valueOf(150.00)); // Przykładowa zmiana ceny
        newOrder.setOrderDate(LocalDateTime.now());
        newOrder.setDeliveryAddress("45 Different Street, Another City");
        Order savedOrder = orderRepository.save(newOrder); // Zapisz zamówienie

        // Zamiana obiektu Order na JSON przy użyciu ObjectMapper
        String orderJson = objectMapper.writeValueAsString(newOrder);

        // Wykonanie żądania POST z nowym zamówieniem
        mockMvc.perform(post("/api/orders")
                        .contentType(APPLICATION_JSON)
                        .with(csrf())
                        .content(orderJson))
                .andExpect(status().isCreated()) // Sprawdzenie, czy odpowiedź ma status 201 Created
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("id", notNullValue())); // Sprawdzenie, czy zamówienie ma wygenerowane ID
    }

    @Test
    @WithMockUser(username = "adminuser", roles = {"ADMIN"})
    public void testUpdateOrder() throws Exception {
        Long orderId = order.getId();
        order.setOrderStatus("UPDATED"); // Zmiana na "dupa"
        order.setTotalPrice(BigDecimal.valueOf(150.00));

        String orderJson = objectMapper.writeValueAsString(order); // Użycie poprawnego obiektu

        mockMvc.perform(put("/api/orders/{id}", orderId)
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(orderJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("orderStatus", is("UPDATED"))) // Sprawdzanie, czy status został zaktualizowany
                .andExpect(jsonPath("totalPrice", is(150.00)));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    public void testDeleteOrder() throws Exception {
        Long orderId = order.getId(); // Pobranie ID z istniejącego zamówienia

        mockMvc.perform(delete("/api/orders/{id}", orderId)
                        .with(csrf()))
                .andExpect(status().isNoContent());
        // Sprawdzenie, czy zamówienie zostało usunięte
        mockMvc.perform(get("/api/orders/{id}", orderId))
                .andExpect(status().isNotFound());
    }
}
