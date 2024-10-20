package projektZajavka2.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import projektZajavka2.entity.RestaurantOwner;
import projektZajavka2.service.RestaurantOwnerService;

import java.util.Optional;

import static org.mockito.Mockito.when;
@ActiveProfiles("test")
@WebMvcTest(RestaurantOwnerController.class)
@WithMockUser(username = "testuser", roles = {"ADMIN"})
public class RestaurantOwnerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RestaurantOwnerService ownerService;

    @Test
    public void shouldReturnOwnerById() throws Exception {
        // Given
        Long id = 1L;
        RestaurantOwner owner = new RestaurantOwner();
        owner.setId(id);
        owner.setName("Test Owner");
        owner.setEmail("owner@example.com");

        when(ownerService.findOwnerById(id)).thenReturn(Optional.of(owner)); // Mockowanie, że właściciel istnieje

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/restaurant-owners/" + id))  // Wykonanie zapytania GET
                .andExpect(MockMvcResultMatchers.status().isOk()) // Sprawdzenie, czy odpowiedź to 200 OK
                .andExpect(MockMvcResultMatchers.view().name("restaurantowners/details")) // Sprawdzenie widoku
                .andExpect(MockMvcResultMatchers.model().attribute("owner", owner)); // Sprawdzenie modelu
    }

    @Test
    public void shouldReturnNotFoundWhenOwnerDoesNotExist() throws Exception {
        // Given
        Long id = 1L;
        when(ownerService.findOwnerById(id)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/restaurant-owners/" + id))
                .andExpect(MockMvcResultMatchers.status().isNotFound()); // Oczekujemy statusu 404
    }
}
