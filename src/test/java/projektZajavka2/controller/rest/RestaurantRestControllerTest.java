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
import projektZajavka2.entity.Restaurant;
import projektZajavka2.entity.RestaurantOwner;
import projektZajavka2.repository.RestaurantOwnerRepository;
import projektZajavka2.repository.RestaurantRepository;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc // Dodaj tę adnotację
@Transactional
@ActiveProfiles("test")
public class RestaurantRestControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @LocalServerPort
    private int port;

    private RestaurantOwner owner;

    private Restaurant restaurant;
    @Autowired
    private MockMvc mockMvc; // Pole dla MockMvc

    @Autowired
    private RestaurantOwnerRepository restaurantOwnerRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;


        owner = new RestaurantOwner();
        owner.setName("Jakis Właściciel");
        owner.setEmail("właściciel@example.com");  // Dodaj wymagany email
        owner.setPhoneNumber("+123456789");        // Dodaj wymagany numer telefonu
        owner.setCreatedAt(LocalDateTime.now());

        // Zapisanie właściciela w bazie danych, aby był zarządzany przez JPA
        owner = restaurantOwnerRepository.save(owner);
        System.out.println("owner: " + owner);

        // Tworzenie restauracji z przypisanym właścicielem
        restaurant = new Restaurant();
        restaurant.setOwner(owner); // Ustaw właściciela, który jest już w bazie
        restaurant.setName("Nowa Restauracja");
        restaurant.setAddress("Ulica 123, Miasto");
        restaurant.setPhoneNumber("+1234567890");
        restaurant.setEmail("nowa@restauracja.com");
        restaurant.setCreatedAt(LocalDateTime.now());

        // Zapisz restaurację w repozytorium
        restaurantRepository.save(restaurant);

        System.out.println("Restauracja zapisana: " + restaurant);
    }

    // Test GET /api/restaurants
    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    public void powinnoZwrocicWszystkieRestauracje() throws Exception {
        mockMvc.perform(get("/api/restaurants")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1))) // Sprawdź liczbę restauracji
                .andExpect(jsonPath("$[0].name").value("Nowa Restauracja")); // Upewnij się, że klucz jest zgodny z danymi restauracji
    }

    // Test GET /api/restaurants/{id}
    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    public void powinnoZwrocicRestauracjePoId() throws Exception {
        Long restaurantId = restaurant.getId(); // Przykładowy identyfikator restauracji
        mockMvc.perform(get("/api/restaurants/" + restaurantId) // Użyj identyfikatora w żądaniu
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(restaurantId)) // Sprawdź, czy zwrócone id jest zgodne
                .andExpect(jsonPath("$.name").value("Nowa Restauracja")); // Upewnij się, że nazwa jest zgodna z danymi restauracji
    }

    // Test GET /api/restaurants/owner/{ownerId}
    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    public void powinnoZwrocicRestauracjePoOwnerId() throws Exception {
        long ownerId = owner.getId(); // Przykładowy identyfikator właściciela

        // Upewnij się, że w bazie danych są restauracje przypisane do tego właściciela
        String expectedRestaurantName = "Nowa Restauracja"; // Oczekiwana nazwa restauracji

        mockMvc.perform(get("/api/restaurants/owner/" + ownerId) // Użyj identyfikatora właściciela w żądaniu
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Sprawdź, czy status odpowiedzi to 200 OK
                .andExpect(jsonPath("$", hasSize(greaterThan(0)))) // Sprawdź, czy zwrócone restauracje nie są puste
                .andExpect(jsonPath("$[0].name").value(expectedRestaurantName)); // Sprawdź, czy nazwa pierwszej restauracji jest zgodna
    }

    // Test GET /api/restaurants/{id}
    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"}) // Użyj mock użytkownika z rolą ADMIN
    public void shouldReturnRestaurantWhenIdExists() throws Exception {
        long restaurantId = restaurant.getId(); // Zakładamy, że restauracja została stworzona w metodzie setUp

        // Wykonaj żądanie GET do endpointu z odpowiednim identyfikatorem
        mockMvc.perform(get("/api/restaurants/{id}", restaurantId) // Użyj identyfikatora w żądaniu
                        .contentType(MediaType.APPLICATION_JSON)) // Ustaw typ treści na JSON
                .andExpect(status().isOk()) // Sprawdź, czy status odpowiedzi to 200 OK
                .andExpect(jsonPath("$.id").value(restaurantId)) // Sprawdź, czy zwrócone id jest zgodne
                .andExpect(jsonPath("$.name").value("Nowa Restauracja")); // Upewnij się, że nazwa jest zgodna z danymi restauracji
    }


    // Test GET /api/restaurants/{id} - restauracja nie istnieje
    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    public void powinnoZwracac404DlaNieistniejacejRestauracji() throws Exception {
        long restaurantId = 999L; // Przykładowy identyfikator, który nie istnieje
        mockMvc.perform(get("/api/restaurants/" + restaurantId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()); // Sprawdź, czy zwracany jest status 404
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    public void shouldCreateNewRestaurant() throws Exception {
        // Given - ustawienie nowych danych testowych
        RestaurantOwner newOwner = new RestaurantOwner();
        newOwner.setName("Nowy Właściciel");
        newOwner.setEmail("nowywłaściciel@example.com");
        newOwner.setPhoneNumber("+1234567891");
        newOwner.setCreatedAt(LocalDateTime.now());

        // Zapisanie nowego właściciela w bazie danych
        newOwner = restaurantOwnerRepository.save(newOwner);
        System.out.println("Nowy owner: " + newOwner);

        Restaurant restaurant = new Restaurant();
        restaurant.setOwner(newOwner); // Ustaw nowego właściciela
        restaurant.setName("Inna Restauracja");
        restaurant.setAddress("Inna Ulica 789, Miasto");
        restaurant.setPhoneNumber("+1234567892");
        restaurant.setEmail("inna@restauracja.com");
        restaurant.setCreatedAt(LocalDateTime.now());

        // Konwersja obiektu restauracji na JSON z wstrzykniętego objectMapper
        String restaurantJson = objectMapper.writeValueAsString(restaurant);

        // When - wykonanie żądania HTTP POST
        mockMvc.perform(post("/api/restaurants")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(restaurantJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(restaurant.getName()))
                .andExpect(jsonPath("$.address").value(restaurant.getAddress()));
    }


    // Test PUT /api/restaurants/{id}
    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    public void shouldUpdateExistingRestaurant() throws Exception {
        // Zakładamy, że restauracja o ID 1 istnieje w bazie danych
        long restaurantId = restaurant.getId();

        // Given - przygotowanie zaktualizowanych danych restauracji
        Restaurant updatedRestaurant = new Restaurant();
        updatedRestaurant.setName("Zaktualizowana Restauracja");
        updatedRestaurant.setAddress("Nowy Adres 456, Miasto");
        updatedRestaurant.setPhoneNumber("+0987654321");
        updatedRestaurant.setEmail("zaktualizowana@restauracja.com");
        updatedRestaurant.setCreatedAt(LocalDateTime.now());

        // Konwersja zaktualizowanej restauracji na JSON
        String updatedRestaurantJson = objectMapper.writeValueAsString(updatedRestaurant);

        // When - wykonanie żądania HTTP PUT
        mockMvc.perform(put("/api/restaurants/{id}", restaurantId)
                        .with(csrf()) // Dodanie CSRF tokena dla metody PUT
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedRestaurantJson)) // Wysłanie danych jako JSON
                .andExpect(status().isOk()) // Oczekiwanie, że status HTTP to 200 OK
                .andExpect(jsonPath("$.name").value(updatedRestaurant.getName())) // Sprawdzenie, czy nazwa została zaktualizowana
                .andExpect(jsonPath("$.address").value(updatedRestaurant.getAddress())) // Sprawdzenie adresu
                .andExpect(jsonPath("$.phoneNumber").value(updatedRestaurant.getPhoneNumber())) // Sprawdzenie numeru telefonu
                .andExpect(jsonPath("$.email").value(updatedRestaurant.getEmail())); // Sprawdzenie emaila
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    public void shouldReturnNotFoundWhenUpdatingNonExistingRestaurant() throws Exception {

        long nonExistingRestaurantId = 999L;

        Restaurant updatedRestaurant = new Restaurant();
        updatedRestaurant.setName("Nieistniejąca Restauracja");
        updatedRestaurant.setAddress("Adres Nieistniejący");
        updatedRestaurant.setPhoneNumber("+123456789");
        updatedRestaurant.setEmail("nieistniejaca@restauracja.com");

        // Konwersja zaktualizowanej restauracji na JSON
        String updatedRestaurantJson = objectMapper.writeValueAsString(updatedRestaurant);

        mockMvc.perform(put("/api/restaurants/{id}", nonExistingRestaurantId)
                        .with(csrf()) // Dodanie CSRF tokena dla metody PUT
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedRestaurantJson)) // Wysłanie danych jako JSON
                .andExpect(status().isNotFound()); // Oczekiwanie, że status HTTP to 404 Not Found
    }

    // Test DELETE /api/restaurants/{id}
    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    public void shouldDeleteRestaurantWhenIdExists() throws Exception {
        long restaurantId = restaurant.getId(); // Zakładamy, że restauracja o ID 1 istnieje

        // When - wykonanie żądania HTTP DELETE
        mockMvc.perform(delete("/api/restaurants/{id}", restaurantId)
                        .with(csrf())) // Dodanie CSRF tokena dla metody DELETE
                .andExpect(status().isNoContent()); // Oczekiwanie, że status HTTP to 204 No Content
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    public void shouldReturnNotFoundWhenDeletingNonExistingRestaurant() throws Exception {
        long nonExistingRestaurantId = 999L; // Zakładamy, że restauracja o ID 999 nie istnieje

        // When - wykonanie żądania HTTP DELETE
        mockMvc.perform(delete("/api/restaurants/{id}", nonExistingRestaurantId)
                        .with(csrf())) // Dodanie CSRF tokena dla metody DELETE
                .andExpect(status().isNotFound()); // Oczekiwanie, że status HTTP to 404 Not Found
    }
}
