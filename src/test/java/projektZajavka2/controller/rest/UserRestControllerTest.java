package projektZajavka2.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.transaction.annotation.Transactional;
import projektZajavka2.entity.User;
import projektZajavka2.repository.UserRepository;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class UserRestControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MockMvc mockMvc;

    private User user;

    @LocalServerPort
    private int port;


    @BeforeEach
    public void setUp() {

        User user = new User();
        user.setUsername("newuser");
        user.setPassword(passwordEncoder.encode("newpassword"));
        user.setEmail("newuser@example.com");
        userRepository.save(user);

        // Zweryfikuj, czy użytkownik został zapisany
        // assertNotNull(userRepository.findByEmail("newuser@example.com"), "Użytkownik powinien być zapisany.");

        System.out.println("czy zapisany: " + user);
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    public void powinnoZwrocicWszystkichUzytkownikow() throws Exception {

        mockMvc.perform(get("/api/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1))) // Sprawdź liczbę użytkowników
                .andExpect(jsonPath("$[0].username").value("newuser")); // Upewnij się, że klucz jest małymi literami
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"}) // Użytkownik z rolą ADMIN do autoryzacji
    public void powinnoZwrocicUzytkownikaPoId() throws Exception {
        Long userId = userRepository.findAll().get(0).getId(); // Pobierz ID użytkownika, który został zapisany

        mockMvc.perform(get("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Sprawdź, czy status odpowiedzi to 200 OK
                .andExpect(jsonPath("$.id").value(userId)) // Sprawdź ID użytkownika
                .andExpect(jsonPath("$.username").value("newuser")) // Upewnij się, że klucz jest małymi literami
                .andExpect(jsonPath("$.email").value("newuser@example.com")); // Sprawdź adres e-mail
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    public void powinnoZwrocicNotFoundDlaNieistniecegoUzytkownika() throws Exception {
        Long nonExistingUserId = 10L; // użytkownik o ID 10 nie istnieje

        mockMvc.perform(get("/api/users/{id}", nonExistingUserId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()); // Sprawdź, czy zwrócony status to 404
    }

    // Test GET /api/users/username/{username}
    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    public void powinnoZwrocicUzytkownikaPoNazwaUzytkownika() throws Exception {
        mockMvc.perform(get("/api/users/username/{username}", "newuser") // Przekazanie nazwy użytkownika w ścieżce
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("newuser"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    public void powinnoZwrocicNotFoundDlaNieistniecegoNazwaUzytkownika() throws Exception {
        String nonExistingUsername = "nonExistingUser"; // Zakładamy, że użytkownik o nazwie "nonExistingUser" nie istnieje

        mockMvc.perform(get("/api/users/username/{username}", nonExistingUsername)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()) // Sprawdź, czy zwrócony status to 404
                .andExpect(jsonPath("$.error").value("User not found")); // Sprawdź komunikat o błędzie (dostosuj klucz i wartość do swojej implementacji)
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    public void powinnoZwrocicUzytkownikaPoEmailu() throws Exception {
        // Pobierz użytkownika po emailu, aby upewnić się, że istnieje
        User user = userRepository.findByEmail("newuser@example.com");

        // Upewnij się, że użytkownik istnieje
        assertNotNull(user, "Użytkownik powinien istnieć w bazie danych"); // Dodanie wiadomości dla jasności

        String email = user.getEmail(); // Pobierz email z obiektu użytkownika

        mockMvc.perform(get("/api/users/email/{email}", email) // Użyj stałej dla punktu końcowego
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()) // Wydrukuj odpowiedź w celu debugowania
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.username").value(user.getUsername())); // Użyj nazwy użytkownika z obiektu użytkownika
    }


    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    public void powinnoZwrocicNotFoundDlaNieistniecegoEmailu() throws Exception {
        String nonExistingEmail = "nonExisting@example.com"; // Zakładamy, że ten użytkownik nie istnieje

        mockMvc.perform(get("/api/users/email/{email}", nonExistingEmail) // Użyj stałej dla punktu końcowego
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()) // Wydrukuj odpowiedź w celu debugowania
                .andExpect(status().isNotFound()) // Sprawdź, czy status to 404 Not Found
                .andExpect(jsonPath("$.error").value("User not found")); // Sprawdź komunikat o błędzie (dostosuj do swojej implementacji)
    }

    // Test POST /api/users
    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    public void powinnoUtworzycNowegoUzytkownika() throws Exception {
        // Given - ustawienie danych nowego użytkownika
        User userAdmin = new User();
        userAdmin.setUsername("adminUser");
        userAdmin.setPassword(passwordEncoder.encode("adminpassword"));
        userAdmin.setEmail("adminUser@example.com");
        userRepository.save(userAdmin);

        // Konwersja obiektu użytkownika na JSON za pomocą wstrzykniętego ObjectMapper
        String userNewJson = objectMapper.writeValueAsString(userAdmin);

        // When - wykonaj żądanie HTTP POST, aby utworzyć nowego użytkownika
        mockMvc.perform(post("/api/users")
                        .with(csrf())  // Dodaj token CSRF
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userNewJson))  // Przekaż dane w formacie JSON
                // Then - oczekuj, że żądanie zakończy się sukcesem (201 Created)
                .andExpect(status().isCreated())
                // Sprawdź, czy zwrócone dane zawierają nazwę użytkownika
                .andExpect(jsonPath("$.username").value("adminUser"))  // Poprawiono wielkie litery
                // Sprawdź, czy zwrócone dane zawierają email użytkownika
                .andExpect(jsonPath("$.email").value("adminUser@example.com"));  // Poprawiono wielkie litery
    }

    // Test PUT /api/users/{id}
    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    public void powinnoZaktualizowacIstniejacegoUzytkownika() throws Exception {
        //Pobierz ID pierwszego użytkownika z repozytorium nie ze zmiennej user w klasie
        Long userId = userRepository.findAll().get(0).getId();

        // Given - utworzenie nowego obiektu z danymi do aktualizacji
        User userAdmin = new User();
        userAdmin.setId(userId);  // Ustawienie ID istniejącego użytkownika
        userAdmin.setUsername("Tomek");
        userAdmin.setPassword(passwordEncoder.encode("password123"));
        userAdmin.setEmail("tomek@example.com");

        // Konwersja obiektu użytkownika na JSON
        String updatedUserJson = objectMapper.writeValueAsString(userAdmin);

        // When - wykonanie żądania PUT do zaktualizowania użytkownika
        mockMvc.perform(put("/api/users/{id}", userId)  // Użyj ID istniejącego użytkownika z setupu
                        .with(csrf())  // Dodanie tokenu CSRF
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedUserJson))  // Przekazanie danych zaktualizowanego użytkownika
                // Then - oczekiwane rezultaty
                .andExpect(status().isOk())  // Sprawdź, czy status odpowiedzi to 200 OK
                .andExpect(jsonPath("$.username").value("Tomek"))  // Upewnij się, że nazwa użytkownika została zaktualizowana
                .andExpect(jsonPath("$.email").value("tomek@example.com"));  // Upewnij się, że email został zaktualizowany
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    public void powinnoZwrocicNotFoundDlaAktualizacjiNieistniejącegoUzytkownika() throws Exception {
        Long nonExistingUserId = 999L; // Zakładamy, że użytkownik o ID 999 nie istnieje
        User updatedUser = new User();
        updatedUser.setUsername("nonExistingUser");
        updatedUser.setEmail("nonexisting@example.com");

        // Konwersja obiektu użytkownika na JSON
        String updatedUserJson = objectMapper.writeValueAsString(updatedUser);

        // When - wykonanie żądania PUT do zaktualizowania nieistniejącego użytkownika
        mockMvc.perform(put("/api/users/{id}", nonExistingUserId)  // Użyj ID nieistniejącego użytkownika
                        .with(csrf())  // Dodaj token CSRF
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedUserJson))  // Przekazanie danych zaktualizowanego użytkownika
                // Then - oczekiwane rezultaty
                .andExpect(status().isNotFound());  // Sprawdź, czy status odpowiedzi to 404 Not Found
    }


    // Test DELETE /api/users/{id}
    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    public void powinnoUsunacUzytkownika() throws Exception {
        Long userId = userRepository.findAll().get(0).getId(); // Zakładamy, że użytkownik o ID 1 istnieje

        // When - wykonanie żądania DELETE w celu usunięcia użytkownika
        mockMvc.perform(delete("/api/users/{id}", userId)  // Użyj ID istniejącego użytkownika
                        .with(csrf())  // Dodaj token CSRF
                        .contentType(MediaType.APPLICATION_JSON))  // Typ zawartości
                // Then - oczekiwane rezultaty
                .andExpect(status().isNoContent());  // Sprawdź, czy status odpowiedzi to 204 No Content
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    public void powinnoZwrocicNotFoundDlaUsuwaniaNieistniecegoUzytkownika() throws Exception {
        Long nonExistingUserId = 999L; // Zakładamy, że użytkownik o ID 999 nie istnieje

        // When - wykonanie żądania DELETE w celu usunięcia nieistniejącego użytkownika
        mockMvc.perform(delete("/api/users/{id}", nonExistingUserId)  // Użyj ID nieistniejącego użytkownika
                        .with(csrf())  // Dodaj token CSRF
                        .contentType(MediaType.APPLICATION_JSON))  // Typ zawartości
                // Then - oczekiwane rezultaty
                .andExpect(status().isNotFound());  // Sprawdź, czy status odpowiedzi to 404 Not Found
    }
}
