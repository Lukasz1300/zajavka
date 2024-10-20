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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import projektZajavka2.entity.MenuItem;
import projektZajavka2.entity.Restaurant;
import projektZajavka2.repository.MenuItemRepository;
import projektZajavka2.repository.RestaurantRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class MenuItemRestControllerTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @LocalServerPort
    private int port;
    private Restaurant restaurant;

    private MenuItem menuItem;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;

        restaurant = new Restaurant();
        restaurant.setName("Test Restaurant");
        restaurant.setAddress("Test Restaurant Address");
        restaurant.setPhoneNumber("+123456789");
        restaurant.setEmail("restaurant@example.com");
        restaurant.setCreatedAt(LocalDateTime.now());
        restaurant = restaurantRepository.save(restaurant);
        System.out.println(restaurant);

        MenuItem menuItem = new MenuItem();
        menuItem.setRestaurant(restaurant);
        menuItem.setName("Pizza");
        menuItem.setPrice(BigDecimal.valueOf(19.99));
        //menuItemRepository.save(menuItem);
        this.menuItem = menuItemRepository.save(menuItem);

        System.out.println(menuItem);

    }

    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    public void testGetAllMenuItems() throws Exception {
        // Testowanie endpointu GET /api/menu-items
        mockMvc.perform(get("/api/menu-items")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("Pizza")); // Sprawdzenie, czy pierwsza pozycja ma nazwę "Pizza"
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    public void testGetMenuItemsByRestaurantId() throws Exception {
        // Przygotowanie danych
        Long restaurantId = restaurant.getId(); // Pobierz ID restauracji

        // Testowanie endpointu GET /api/menu-items/restaurant/{restaurantId}
        mockMvc.perform(get("/api/menu-items/restaurant/{restaurantId}", restaurantId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("Pizza")); // Sprawdzenie, czy pierwsza pozycja ma nazwę "Burger"
    }


    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    public void testCreateMenuItem() throws Exception {
        // Tworzenie restauracji
        restaurant = new Restaurant();
        restaurant.setName("Zielona Kuchnia");
        restaurant.setAddress("ul. Zielona 8, 31-002 Kraków");
        restaurant.setPhoneNumber("+48121234567");
        restaurant.setEmail("zielonakuchnia@example.com");
        restaurant.setCreatedAt(LocalDateTime.now());
        restaurant = restaurantRepository.save(restaurant);

        // Tworzenie nowego obiektu MenuItem
        MenuItem menuItem = new MenuItem();
        menuItem.setRestaurant(restaurant); // Ustaw restaurację
        menuItem.setName("Sałatka"); // Ustaw nazwę pozycji menu na "Sałatka"
        menuItem.setPrice(BigDecimal.valueOf(15.99));
        menuItem = menuItemRepository.save(menuItem);

        // Testowanie endpointu POST /api/menu-items
        mockMvc.perform(post("/api/menu-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(menuItem)) // Konwersja obiektu do JSON
                        .with(csrf())) // Dodanie tokenu CSRF
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Sałatka"))
                .andExpect(jsonPath("$.price").value(15.99));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    public void testUpdateMenuItem() throws Exception {
        // Pobranie istniejącego elementu MenuItem z repozytorium
        Long menuItemId = menuItemRepository.findAll().get(0).getId();
        MenuItem existingMenuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new RuntimeException("MenuItem not found"));

        // Zaktualizowanie danych MenuItem
        existingMenuItem.setName("Nowe Danie");
        existingMenuItem.setPrice(BigDecimal.valueOf(15.50));

        // Testowanie endpointu PUT /api/menu-items/{id}
        mockMvc.perform(MockMvcRequestBuilders.put("/api/menu-items/{id}", menuItemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(existingMenuItem)) // Konwersja obiektu do JSON
                        .with(csrf())) // Dodanie tokenu CSRF
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Nowe Danie")) // Sprawdzenie zaktualizowanej nazwy
                .andExpect(jsonPath("$.price").value(15.50)); // Sprawdzenie zaktualizowanej ceny
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    public void testDeleteMenuItem() throws Exception {
        // Użycie istniejącego menuItem utworzonego w setUp
        Long menuItemId = menuItem.getId(); // Zakładając, że menuItem jest ustawione w setUp

        // Testowanie endpointu DELETE /api/menu-items/{id}
        mockMvc.perform(delete("/api/menu-items/{id}", menuItemId)
                        .with(csrf())) // Dodanie tokenu CSRF
                .andExpect(status().isNoContent());

        // Sprawdzanie, czy pozycja menu została usunięta
        mockMvc.perform(get("/api/menu-items/{id}", menuItemId))
                .andExpect(status().isNotFound()); // Sprawdzenie, czy nie znaleziono pozycji
    }
}
