package projektZajavka2.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import projektZajavka2.entity.Restaurant;
import projektZajavka2.entity.RestaurantOwner;
import projektZajavka2.service.RestaurantService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@ActiveProfiles("test")
@WebMvcTest(RestaurantController.class)
public class RestaurantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RestaurantService restaurantService;

    @Test
    @WithMockUser
    public void shouldReturnAllRestaurants() throws Exception {
        // Given
        List<Restaurant> restaurants = Collections.singletonList(new Restaurant());
        when(restaurantService.findAllRestaurants()).thenReturn(restaurants);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/restaurants"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("restaurants/list"))
                .andExpect(MockMvcResultMatchers.model().attribute("restaurants", restaurants));
    }

    @Test
    @WithMockUser
    public void shouldShowNewRestaurantForm() throws Exception {
        // Given, When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/restaurants/new"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("restaurants/form"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("restaurant"));
    }

    @Test
    @WithMockUser
    public void shouldSaveRestaurant() throws Exception {
        // Given
        Restaurant restaurant = new Restaurant();
        restaurant.setName("Test Restaurant");
        restaurant.setAddress("123 Test St");
        restaurant.setPhoneNumber("123456789");
        restaurant.setEmail("test@example.com");
        restaurant.setDescription("A test restaurant.");
        when(restaurantService.saveRestaurant(restaurant)).thenReturn(restaurant);

        when(restaurantService.saveRestaurant(restaurant)).thenReturn(restaurant);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/restaurants")
                        .with(csrf()) //  uwzględnić CSRF token
                        .flashAttr("restaurant", restaurant))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/restaurants"));
    }

    @Test
    @WithMockUser
    public void shouldReturnRestaurantById() throws Exception {
        // Given
        Long id = 1L;

        RestaurantOwner owner = new RestaurantOwner();
        owner.setId(1L); // Opcjonalne, jeśli potrzebne
        owner.setName("Owner Name");
        owner.setEmail("owner@example.com");
        owner.setPhoneNumber("+123456789");

        Restaurant restaurant = new Restaurant();
        restaurant.setId(id);
        restaurant.setOwner(owner);

        when(restaurantService.findRestaurantById(id)).thenReturn(Optional.of(restaurant));

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/restaurants/" + id))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("restaurants/view"))
                .andExpect(MockMvcResultMatchers.model().attribute("restaurant", restaurant));
    }

    @Test
    @WithMockUser
    public void shouldReturnNotFoundWhenRestaurantDoesNotExist() throws Exception {
        // Given
        Long id = 1L;
        when(restaurantService.findRestaurantById(id)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/restaurants/" + id))
                .andExpect(MockMvcResultMatchers.status().isNotFound()) // Poprawka statusu na 404
                .andExpect(MockMvcResultMatchers.view().name("error/404")); // Sprawdzenie widoku błędu
    }

    @Test
    @WithMockUser
    public void shouldDeleteRestaurantById() throws Exception {
        // Given
        Long id = 1L;
        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.delete("/restaurants/delete/" + id)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/restaurants"));
    }
}

