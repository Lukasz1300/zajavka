package projektZajavka2.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import projektZajavka2.entity.MenuItem;
import projektZajavka2.entity.Restaurant;
import projektZajavka2.repository.MenuItemRepository;
import projektZajavka2.repository.RestaurantRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@AutoConfigureMockMvc
@WithMockUser(username = "testuser", roles = {"ADMIN"})
@ActiveProfiles("test")
public class MenuItemControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    private Restaurant restaurant;
    private MenuItem menuItem;

    @BeforeEach
    public void setUp() {

        // Tworzenie restauracji
        restaurant = new Restaurant();
        restaurant.setName("Test Restaurant");
        restaurant.setAddress("Test Restaurant Address");
        restaurant.setPhoneNumber("+123456789");
        restaurant.setEmail("restaurant@example.com");
        restaurant.setCreatedAt(LocalDateTime.now());
        restaurant = restaurantRepository.save(restaurant); // Przypisanie do pola klasy
        System.out.println(restaurant);

        // Tworzenie nowego obiektu MenuItem
        MenuItem menuItem = new MenuItem();
        menuItem.setRestaurant(restaurant); // Ustaw restaurację
        menuItem.setName("Pizza"); // Ustaw nazwę pozycji menu na "Pizza"
        menuItem.setPrice(BigDecimal.valueOf(19.99));
        //menuItemRepository.save(menuItem);
        this.menuItem = menuItemRepository.save(menuItem);
        System.out.println(menuItem);
    }

    @Test
    public void testGetAllMenuItems() throws Exception {
        mockMvc.perform(get("/menu-item"))
                .andExpect(status().isOk())
                .andExpect(view().name("menuitem/list"))
                .andExpect(model().attributeExists("menuItems")) // Poprawione na "menuItems"
                .andExpect(model().attribute("menuItems", hasSize(1))) // Poprawione na "menuItems"
                .andExpect(model().attribute("menuItems", hasItem(
                        hasProperty("name", is("Pizza"))
                )));
    }

    @Test
    public void testGetMenuItemsByRestaurantId() throws Exception {
        Restaurant restaurant = restaurantRepository.findAll().get(0);

        mockMvc.perform(get("/menu-item/restaurant/{restaurantId}", restaurant.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("menuitem/list"))
                .andExpect(model().attributeExists("menuItems"))
                .andExpect(model().attribute("menuItems", hasSize(1)))
                .andExpect(model().attribute("menuItems", hasItem(
                        hasProperty("name", is("Pizza"))
                )));
    }

    @Test
    public void testGetMenuItemById() throws Exception {
        MenuItem menuItem = menuItemRepository.findAll().get(0);

        mockMvc.perform(get("/menu-item/{id}", menuItem.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("menuitem/detail"))
                .andExpect(model().attributeExists("menuItem"))
                .andExpect(model().attribute("menuItem", hasProperty("name", is("Pizza"))));
    }

    @Test
    public void testShowNewMenuItemForm() throws Exception {
        mockMvc.perform(get("/menu-item/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("menuitem/form"))
                .andExpect(model().attributeExists("menuItem"));
    }

    @Test
    public void testSaveMenuItem() throws Exception {
        mockMvc.perform(post("/menu-item")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("name", "New Menu Item")
                        .param("price", "25.99")  // Dodaj cenę, jeśli jest wymagana
                        .param("restaurant.id", restaurantRepository.findAll().get(0).getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/menu-item"));

        // Verify the new menu item is saved
        MenuItem savedMenuItem = menuItemRepository.findAll().get(1); // Index 1 because we have 1 existing item
        assertEquals("New Menu Item", savedMenuItem.getName());
    }

    @Test
    public void testDeleteMenuItem() throws Exception {
        // Zakładamy, że w repozytorium jest przynajmniej jeden element
        MenuItem menuItem = menuItemRepository.findAll().get(0);

        mockMvc.perform(delete("/menu-item/delete/{id}", menuItem.getId())  // Użycie metody DELETE
                        .with(SecurityMockMvcRequestPostProcessors.csrf())) // Zamknięcie wywołania .with(...)
                .andExpect(status().is3xxRedirection()) // Sprawdzenie, czy status to 3xx (przekierowanie)
                .andExpect(redirectedUrl("/menu-item")); // Sprawdzenie, czy przekierowano na odpowiednią stronę

        // Weryfikacja, że element został usunięty
        assertEquals(0L, menuItemRepository.count()); // count() zwraca long, więc używamy 0L
    }

}
