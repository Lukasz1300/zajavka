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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import projektZajavka2.entity.Category;
import projektZajavka2.entity.MenuItem;
import projektZajavka2.entity.Restaurant;
import projektZajavka2.repository.CategoryRepository;
import projektZajavka2.repository.MenuItemRepository;
import projektZajavka2.repository.RestaurantRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class CategoryRestControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @LocalServerPort
    private int port;

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private RestaurantRepository restaurantRepository;
    @Autowired
    private MenuItemRepository menuItemRepository;

    private Restaurant restaurant;

    private MenuItem menuItem;
    private Category category;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() throws Exception {
        RestAssured.port = port;

        Category category = new Category();
        category.setName("Test Category");
        this.category = categoryRepository.save(category);
        System.out.println(category);
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    public void testGetAllCategories() throws Exception {
        // Testowanie endpointu GET /api/categories
        mockMvc.perform(get("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1))) // czy jest jedna kategoria
                .andExpect(jsonPath("$[0].name").value("Test Category")); // Sprawdź nazwę kategorii
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    public void testCreateAndGetCategory() throws Exception {
        // Tworzenie restauracji
        restaurant = new Restaurant();
        restaurant.setName("Zielona Kuchnia");
        restaurant.setAddress("ul. Zielona 8, 31-002 Kraków");
        restaurant.setPhoneNumber("+48121234567");
        restaurant.setEmail("zielonakuchnia@example.com");
        restaurant.setCreatedAt(LocalDateTime.now());
        restaurant = restaurantRepository.save(restaurant);

        Category newCategory = new Category();
        newCategory.setName("New Category");
        newCategory = categoryRepository.save(newCategory);

        MenuItem menuItem = new MenuItem();
        menuItem.setRestaurant(restaurant); // Ustaw restaurację
        menuItem.setName("Sałatka"); // Ustaw nazwę pozycji menu na "Sałatka"
        menuItem.setPrice(BigDecimal.valueOf(15.99));
        menuItem.getCategories().add(newCategory); // Dodaj nową kategorię do MenuItem
        this.menuItem = menuItemRepository.save(menuItem); // Zapisz MenuItem

        // Wysyłanie żądania POST do utworzenia nowej kategorii
        mockMvc.perform(MockMvcRequestBuilders.post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCategory))
                        .with(csrf())) // Dodanie tokenu CSRF
                .andExpect(status().isCreated()) // Oczekiwany status 201 Created
                .andExpect(jsonPath("$.name").value("New Category"));
        System.out.println("ID nowej kategorii: " + newCategory.getId() + ", Nazwa: " + newCategory.getName());


        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.name == 'New Category')]").exists());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    public void testUpdateCategory() throws Exception {
        // Nowe dane do aktualizacji
        Category updatedCategory = new Category();
        updatedCategory.setName("Updated Category");

        // Testowanie endpointu PUT /api/categories/{id}
        mockMvc.perform(MockMvcRequestBuilders.put("/api/categories/{id}", category.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedCategory)) // Konwersja obiektu do JSON
                        .with(csrf())) // Dodanie tokenu CSRF
                .andExpect(status().isOk()) // Oczekiwany status 200 OK
                .andExpect(jsonPath("$.name").value("Updated Category")); // Sprawdzenie nowej nazwy kategorii

        // czy kategoria została zaktualizowana w bazie danych
        Category retrievedCategory = categoryRepository.findById(category.getId()).orElse(null);
        assertNotNull(retrievedCategory);
        assertEquals("Updated Category", retrievedCategory.getName());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testDeleteCategory() throws Exception {
        // w setUp masz utworzoną kategorię i zapisujesz ją w savedCategory
        assertNotNull(category); // Sprawdzenie, czy kategoria została utworzona

        // Testowanie endpointu DELETE /api/categories/{id} z tokenem CSRF
        mockMvc.perform(delete("/api/categories/{id}", category.getId())
                        .with(csrf())) // Dodanie tokenu CSRF
                .andDo(print()) // Debug output
                .andExpect(status().isNoContent()); // Oczekiwany status 204 No Content

        // Weryfikacja, czy kategoria została usunięta poprzez sprawdzenie, że nie istnieje w bazie
        mockMvc.perform(get("/api/categories")
                        .with(csrf())) // Dodanie tokenu CSRF
                .andExpect(status().isOk()) // Oczekiwany status 200 OK
                .andExpect(jsonPath("$", hasSize(0))); // Sprawdzenie, że lista kategorii jest pusta
    }
}

