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
import projektZajavka2.entity.Restaurant;
import projektZajavka2.entity.RestaurantOwner;
import projektZajavka2.repository.RestaurantOwnerRepository;
import projektZajavka2.repository.RestaurantRepository;

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
public class RestaurantControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private RestaurantOwnerRepository restaurantOwnerRepository;
    private Long savedOwnerId;


    @BeforeEach
    public void setUp() {

        RestaurantOwner owner = new RestaurantOwner();
        owner.setName("Test Owner");
        owner.setEmail("owner@test.com");
        owner.setPhoneNumber("+1234567890");
        owner.setCreatedAt(LocalDateTime.now());
        restaurantOwnerRepository.save(owner);
        savedOwnerId = restaurantOwnerRepository.save(owner).getId();

        Restaurant restaurant = new Restaurant();
        restaurant.setOwner(owner);
        restaurant.setName("Test Restaurant");
        restaurant.setAddress("123 Test Street");
        restaurant.setPhoneNumber("+1234567890");
        restaurant.setEmail("restaurant@test.com");
        restaurant.setDescription("Test Description");
        restaurant.setCreatedAt(LocalDateTime.now());
        restaurantRepository.save(restaurant);
    }

    @Test
    public void testGetAllRestaurants() throws Exception {
        mockMvc.perform(get("/restaurants"))
                .andExpect(status().isOk())
                .andExpect(view().name("restaurants/list"))
                .andExpect(model().attributeExists("restaurants"))
                .andExpect(model().attribute("restaurants", hasSize(1)))
                .andExpect(model().attribute("restaurants", hasItem(
                        allOf(
                                hasProperty("name", is("Test Restaurant")),
                                hasProperty("address", is("123 Test Street"))
                        )
                )));
    }

    @Test
    public void testGetRestaurantById() throws Exception {
        Restaurant restaurant = restaurantRepository.findAll().get(0);

        mockMvc.perform(get("/restaurants/{id}", restaurant.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("restaurants/view"))
                .andExpect(model().attributeExists("restaurant"))
                .andExpect(model().attribute("restaurant", hasProperty("name", is("Test Restaurant"))));
    }

    @Test
    public void testGetRestaurantsByOwnerId() throws Exception {

        mockMvc.perform(get("/restaurants/owner/{ownerId}", savedOwnerId))
                .andExpect(status().isOk())
                .andExpect(view().name("restaurants/list"))
                .andExpect(model().attributeExists("restaurants"))
                .andExpect(model().attribute("restaurants", hasSize(1)))
                .andExpect(model().attribute("restaurants", hasItem(
                        allOf(
                                hasProperty("name", is("Test Restaurant")),
                                hasProperty("address", is("123 Test Street"))
                        )
                )));
    }

    @Test
    public void testShowNewRestaurantForm() throws Exception {
        mockMvc.perform(get("/restaurants/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("restaurants/form"))
                .andExpect(model().attributeExists("restaurant"));
    }

    @Test
    public void testSaveRestaurant() throws Exception {
        RestaurantOwner owner = restaurantOwnerRepository.findAll().get(0);

        mockMvc.perform(post("/restaurants")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("owner.id", owner.getId().toString())
                        .param("name", "New Restaurant")
                        .param("address", "456 New Street")
                        .param("phoneNumber", "+0987654321")
                        .param("email", "newrestaurant@test.com")
                        .param("description", "New Description"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/restaurants"));

        // Verify the new restaurant is saved
        Restaurant savedRestaurant = restaurantRepository.findAll().get(1); // Index 1 because we have 1 existing item
        assertEquals("New Restaurant", savedRestaurant.getName());
        assertEquals("456 New Street", savedRestaurant.getAddress());
    }

    @Test
    public void testDeleteRestaurant() throws Exception {
        Restaurant restaurant = restaurantRepository.findAll().get(0);

        mockMvc.perform(delete("/restaurants/delete/{id}", restaurant.getId())
                        .with(SecurityMockMvcRequestPostProcessors.csrf())) // Dodaj CSRF, jeśli jest wymagany
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/restaurants"));
        // Verify the restaurant is deleted
        assertEquals(0, restaurantRepository.count());
    }
}



