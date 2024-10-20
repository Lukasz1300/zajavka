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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import projektZajavka2.entity.*;
import projektZajavka2.repository.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@WithMockUser(username = "testuser", roles = {"ADMIN"})
@ActiveProfiles("test")
public class OrderItemRestControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private RestaurantRepository restaurantRepository;
    @Autowired
    private MenuItemRepository menuItemRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ObjectMapper objectMapper; // Dodaj to do swojego testu

    private User user;
    private Restaurant restaurant;

    private Order order;

    private MenuItem menuItem;

    private OrderItem orderItem;


    @LocalServerPort
    private int port;

    @BeforeEach
    public void setup() {
        RestAssured.port = port;

        // Tworzenie użytkownika
        user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setEmail("testuser@example.com");
        user = userRepository.save(user); // Przypisanie do pola klasy
        System.out.println("user: " + user);

        // Tworzenie restauracji
        restaurant = new Restaurant();
        restaurant.setName("Test Restaurant");
        restaurant.setAddress("Test Restaurant Address");
        restaurant.setPhoneNumber("+123456789");
        restaurant.setEmail("restaurant@example.com");
        restaurant.setCreatedAt(LocalDateTime.now());
        restaurant = restaurantRepository.save(restaurant); // Przypisanie do pola klasy
        System.out.println("restaurant: " + restaurant);

        // Tworzenie pozycji menu
        menuItem = new MenuItem();
        menuItem.setName("Test Menu Item");
        menuItem.setPrice(BigDecimal.valueOf(50.00));
        menuItem.setRestaurant(restaurant);
        menuItem = menuItemRepository.save(menuItem); // Przypisanie do pola klasy
        System.out.println("menuItem: " + menuItem);

        // Tworzenie zamówienia
        order = new Order();
        order.setUser(user);
        order.setRestaurant(restaurant);
        order.setOrderStatus("PENDING");
        order.setTotalPrice(BigDecimal.valueOf(100.00));
        order.setDeliveryAddress("Test Address");
        order.setOrderDate(LocalDateTime.now());
        order = orderRepository.save(order); // Przypisanie do pola klasy

        // Tworzenie OrderItem
        orderItem = new OrderItem();
        orderItem.setOrder(order); // Powiązanie z zapisanym zamówieniem
        orderItem.setMenuItem(menuItem);
        orderItem.setQuantity(2);
        orderItem.setPrice(menuItem.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())));

        // Zapisanie OrderItem do bazy
        orderItem = orderItemRepository.save(orderItem); // Przypisanie do pola klasy

        System.out.println("order: " + order);
        System.out.println("orderItem: " + orderItem);
    }


    @Test
    public void testGetAllOrderItems() throws Exception {
        mockMvc.perform(get("/api/order-items") // Wykonanie żądania GET na endpoint /api/order-items
                        .contentType(MediaType.APPLICATION_JSON)) // Ustawienie typu treści jako JSON
                .andExpect(status().isOk()) // Oczekiwanie na status HTTP 200 (OK)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)) // Sprawdzenie, czy typ odpowiedzi to JSON
                .andExpect(jsonPath("$").isArray()) // Sprawdzenie, czy odpowiedź jest tablicą JSON
                .andExpect(jsonPath("$[0].id").exists()) // Sprawdzenie, czy pierwszy element tablicy ma pole 'id'
                .andExpect(jsonPath("$[0].quantity").isNumber()); // Sprawdzenie, czy pole 'quantity' jest liczbą
    }

    @Test
    public void testGetOrderItemById() throws Exception {
        Long orderItemId = orderItem.getId(); // Pobierz ID OrderItem

        mockMvc.perform(get("/api/order-items/{id}", orderItemId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(orderItemId));
    }

    @Test
    public void testCreateOrderItem() throws Exception {
        // Tworzenie nowego użytkownika
        User newUser = new User();
        newUser.setUsername("newTestUser"); // Użyj unikalnej nazwy użytkownika
        newUser.setPassword("password123"); // Możesz użyć tej samej lub innej
        newUser.setEmail("newTestUser@example.com"); // Użyj unikalnego adresu e-mail
        userRepository.save(newUser);

        // Tworzenie zamówienia dla nowego użytkownika
        Order order = new Order();
        order.setUser(newUser); // Ustaw nowego użytkownika
        order.setRestaurant(restaurant); // Użyj istniejącej restauracji
        order.setOrderStatus("PENDING");
        order.setTotalPrice(BigDecimal.valueOf(100.00));
        order.setDeliveryAddress("Test Address");
        order.setOrderDate(LocalDateTime.now());
        order = orderRepository.save(order); // Zapisz nowe zamówienie

        // Tworzenie pozycji zamówienia
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order); // Powiązanie z zapisanym zamówieniem
        orderItem.setMenuItem(menuItem); // Użyj istniejącego menu
        orderItem.setQuantity(2);
        orderItem.setPrice(menuItem.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())));

        // Wysłanie żądania POST do utworzenia OrderItem
        mockMvc.perform(post("/api/order-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(orderItem))) // Serializacja obiektu do JSON
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", notNullValue()));
    }

    @Test
    public void testUpdateMenuItem() throws Exception {
        // Pobranie istniejącego elementu MenuItem z repozytorium
        Long menuItemId = menuItemRepository.findAll().get(0).getId();
        MenuItem existingMenuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new RuntimeException("MenuItem not found"));

        // Zaktualizowanie danych MenuItem
        existingMenuItem.setName("Nowe Danie");
        existingMenuItem.setPrice(BigDecimal.valueOf(15.50));

        // Testowanie endpointu PUT /api/menu-items/{id}
        mockMvc.perform(put("/api/menu-items/{id}", menuItemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(existingMenuItem)) // Konwersja obiektu do JSON
                        .with(csrf())) // Dodanie tokenu CSRF
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Nowe Danie")) // Sprawdzenie zaktualizowanej nazwy
                .andExpect(jsonPath("$.price").value(15.50)); // Sprawdzenie zaktualizowanej ceny
    }

    @Test
    public void testDeleteOrderItem() throws Exception {
        // Long orderItemId = 1L;
        Long orderItemId = orderItem.getId();

        // Wysłanie żądania DELETE do usunięcia OrderItem
        mockMvc.perform(delete("/api/order-items/{id}", orderItemId)
                        .with(csrf())) // Dodanie tokenu CSRF
                .andExpect(status().isNoContent());

        // Sprawdzenie, czy element został usunięty
        mockMvc.perform(get("/api/order-items/{id}", orderItemId))
                .andExpect(status().isNotFound());
    }
}
