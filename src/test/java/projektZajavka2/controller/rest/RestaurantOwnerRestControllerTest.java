package projektZajavka2.controller.rest;

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
import projektZajavka2.entity.RestaurantOwner;
import projektZajavka2.repository.RestaurantOwnerRepository;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class RestaurantOwnerRestControllerTest {

    @Autowired
    private MockMvc mockMvc; // Pole dla MockMvc

    @Autowired
    private RestaurantOwnerRepository restaurantOwnerRepository;

    private RestaurantOwner owner;

    @LocalServerPort
    private int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;

        // Tworzenie właściciela
        owner = new RestaurantOwner();
        owner.setName("Jakis Właściciel");
        owner.setEmail("właściciel@example.com");  // Dodaj wymagany email
        owner.setPhoneNumber("+123456789");        // Dodaj wymagany numer telefonu
        owner.setCreatedAt(LocalDateTime.now());

        // Zapisanie właściciela w bazie danych, aby był zarządzany przez JPA
        owner = restaurantOwnerRepository.save(owner);
        System.out.println("owner: " + owner);

    }

    // Test dla metody GET /api/restaurant-owners/{id}
    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    public void powinnoZwrocicRestauracjePoId() throws Exception {
        long restaurantOwnerId = owner.getId(); // Pobranie rzeczywistego ID właściciela z utworzonego obiektu w bazie

        mockMvc.perform(get("/api/restaurant-owners/{id}", restaurantOwnerId) // Użycie poprawnej konstrukcji ścieżki
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(restaurantOwnerId)) // Sprawdzenie, czy zwrócone id jest zgodne
                .andExpect(jsonPath("$.name").value("Jakis Właściciel")); // Sprawdzenie, czy imię właściciela jest prawidłowe
    }

    //  Test dla metody GET /api/restaurant-owners/{id}
    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    public void shouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
        // Given - ustawienie danych testowych
        Long nonExistingOwnerId = 999L; // Zakładam, że właściciel o ID 999 nie istnieje

        // When & Then - wykonanie żądania i sprawdzenie statusu odpowiedzi
        mockMvc.perform(get("/api/restaurant-owners/{id}", nonExistingOwnerId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()); // Sprawdzenie, czy zwraca status 404
    }
}
