package projektZajavka2.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import projektZajavka2.entity.User;
import projektZajavka2.service.UserService;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(UserController.class)
@WithMockUser(username = "testuser", roles = {"ADMIN"})
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    public void shouldReturnAllUsers() throws Exception {
        List<User> users = List.of(new User(), new User());
        when(userService.findAllUsers()).thenReturn(users);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())  // Oczekiwany status 200 OK
                .andExpect(view().name("user/list"))  // Oczekiwany widok
                .andExpect(model().attribute("users", users));  // Oczekiwany model z użytkownikami
    }

    @Test
    public void shouldReturnUserById() throws Exception {
        Long id = 1L;
        User user = new User();
        when(userService.findUserById(id)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/users/" + id))
                .andExpect(status().isOk())
                .andExpect(view().name("user/detail"))
                .andExpect(model().attribute("user", user));
    }

    @Test
    public void shouldReturn404WhenUserNotFound() throws Exception {
        String username = "nonExistentUser";

        // Użycie Optional.empty() do symulacji braku użytkownika
        when(userService.findUserByUserName(username)).thenReturn(Optional.empty());

        mockMvc.perform(get("/users/username/" + username))
                .andExpect(status().isNotFound())
                .andExpect(view().name("error/404"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    public void shouldReturnUserByUserName() throws Exception {
        String username = "testUser";
        User user = new User();
        user.setUsername(username); // Ustawienie username dla obiektu User

        // Użycie Optional do zwrócenia użytkownika
        when(userService.findUserByUserName(username)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/users/username/" + username))
                .andExpect(status().isOk())
                .andExpect(view().name("user/detail"))
                .andExpect(model().attribute("user", user));
    }

    @Test
    public void shouldReturnUserByEmail() throws Exception {
        String email = "test@example.com";
        User user = new User();
        when(userService.findUserByEmail(email)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/users/email/" + email))
                .andExpect(status().isOk())
                .andExpect(view().name("user/detail"))
                .andExpect(model().attribute("user", user));
    }

    @Test
    public void shouldReturn404WhenUserByEmailNotFound() throws Exception {
        // Given
        String email = "test@example.com";
        when(userService.findUserByEmail(email)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/users/email/" + email))
                .andExpect(status().isNotFound())  // Oczekiwany kod statusu 404
                .andExpect(view().name("error/404")) // Oczekiwany widok 404
                .andExpect(model().attribute("error", "User not found"));  // Sprawdza, czy model zawiera odpowiedni komunikat
    }

    @Test
    public void shouldShowNewUserForm() throws Exception {

        mockMvc.perform(get("/users/new"))

                .andExpect(status().isOk())
                .andExpect(view().name("user/form"))
                .andExpect(model().attribute("user", allOf(
                        hasProperty("id", is(nullValue())),
                        hasProperty("username", is(nullValue())),
                        hasProperty("password", is(nullValue())),
                        hasProperty("email", is(nullValue())),
                        hasProperty("firstName", is(nullValue())),
                        hasProperty("lastName", is(nullValue())),
                        hasProperty("phoneNumber", is(nullValue())),
                        hasProperty("address", is(nullValue()))
                )));
    }

    @Test
    public void shouldSaveUser() throws Exception {

        User user = new User();

        when(userService.saveUser(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/users")
                        .flashAttr("user", user)
                        .with(csrf())) // Dodaj token CSRF
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"));

        verify(userService, times(1)).saveUser(any(User.class));
    }

    @Test
    public void shouldDeleteUser() throws Exception {
        // Given
        Long id = 1L;
        // When & Then
        mockMvc.perform(delete("/users/delete/" + id)
                        .with(csrf())) // Dodaj token CSRF
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"));

        verify(userService, times(1)).deleteUserById(id);
    }
}
