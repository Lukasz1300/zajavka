package projektZajavka2.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import projektZajavka2.entity.User;
import projektZajavka2.repository.UserRepository;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@AutoConfigureMockMvc
@WithMockUser(username = "testuser", roles = {"ADMIN"})
@ActiveProfiles("test")
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {

        User testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("testpassword");
        testUser.setEmail("testuser@example.com");
        userRepository.save(testUser);
        System.out.println(testUser);
    }

    @Test
    void testGetAllUsers() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/list"))
                .andExpect(model().attributeExists("users"))
                .andExpect(model().attribute("users", hasSize(1)))
                .andExpect(model().attribute("users", hasItem(
                        allOf(
                                hasProperty("username", is("testuser")),
                                hasProperty("email", is("testuser@example.com"))
                        )
                )));
    }

    @Test
    void testGetUserById() throws Exception {
        // Uzyskanie ID użytkownika
        Long userId = userRepository.findAll().get(0).getId();

        // Wykonanie zapytania do kontrolera
        mockMvc.perform(get("/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(view().name("user/detail"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attribute("user", hasProperty("username", is("testuser"))));
    }

    @Test
    void testGetUserByUsername() throws Exception {
        // Zakładając, użytkownik o nazwie "testuser" w bazie danych testowej
        User userName = userRepository.findByUsername("testuser");

        // Sprawdzenie, czy użytkownik został znaleziony
        assertNotNull(userName, "Użytkownik powinien istnieć");

        mockMvc.perform(get("/users/username/" + userName.getUsername()))  // Użycie nazwy użytkownika
                .andExpect(status().isOk())
                .andExpect(view().name("user/detail"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attribute("user", hasProperty("username", is(userName.getUsername())))); // Użycie nazwy użytkownika
    }

    @Test
    void testGetUserByEmail() throws Exception {
        // Zakładając, że masz użytkownika o adresie e-mail "testuser@example.com" w bazie danych testowej
        User email = userRepository.findByEmail("testuser@example.com");

        // Sprawdzenie, czy użytkownik został znaleziony
        assertNotNull(email, "Użytkownik powinien istnieć");

        mockMvc.perform(get("/users/email/" + email.getEmail()))
                .andExpect(status().isOk())
                .andExpect(view().name("user/detail"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attribute("user", hasProperty("email", is(email.getEmail()))));
    }


    @Test
    void testShowNewUserForm() throws Exception {
        mockMvc.perform(get("/users/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/form"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    void testSaveUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("username", "newuser")
                        .param("password", "newpassword")
                        .param("email", "newuser@example.com")
                        .param("firstName", "New")
                        .param("lastName", "User"))
                .andExpect(status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/users"));
    }

    @Test
    void testDeleteUser() throws Exception {

        User userDelete = userRepository.findByUsername("testuser");

        assertNotNull(userDelete, "Użytkownik powinien istnieć"); // Użycie assertNotNull

        Long userId = userDelete.getId();

        mockMvc.perform(MockMvcRequestBuilders.delete("/users/delete/" + userId) // Użyj DELETE
                        .with(SecurityMockMvcRequestPostProcessors.csrf())) // Dodaj CSRF, jeśli to konieczne
                .andExpect(status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/users"));
    }
}
