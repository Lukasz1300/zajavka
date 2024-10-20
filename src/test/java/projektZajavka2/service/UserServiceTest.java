package projektZajavka2.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import projektZajavka2.entity.User;
import projektZajavka2.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    public void shouldFindAllUsers() {
        // Given
        List<User> expectedUsers = List.of(new User(), new User());
        when(userRepository.findAll()).thenReturn(expectedUsers);

        // When
        List<User> actualUsers = userService.findAllUsers();

        // Then
        assertEquals(expectedUsers, actualUsers);
    }

    @Test
    public void shouldFindUserById() {
        // Given
        Long id = 1L;
        User expectedUser = new User();
        when(userRepository.findById(id)).thenReturn(Optional.of(expectedUser));

        // When
        Optional<User> actualUser = userService.findUserById(id);

        // Then
        assertTrue(actualUser.isPresent());
        assertEquals(expectedUser, actualUser.get());
    }

    @Test
    public void shouldReturnEmptyWhenUserByIdNotFound() {
        // Given
        Long id = 1L;
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        // When
        Optional<User> actualUser = userService.findUserById(id);

        // Then
        assertFalse(actualUser.isPresent());
    }

    @Test
    public void shouldFindUserByUserName() {
        // Given
        String username = "testUser";
        User expectedUser = new User();
        expectedUser.setUsername(username);
        when(userRepository.findByUsername(username)).thenReturn(expectedUser);

        // When
        Optional<User> actualUser = userService.findUserByUserName(username);

        // Then
        assertTrue(actualUser.isPresent());
        assertEquals(expectedUser, actualUser.get()); // Porównanie expectedUser z actualUser.get()
    }

    @Test
    public void shouldSaveUser() {
        // Given
        User user = new User();
        when(userRepository.save(user)).thenReturn(user);

        // When
        User savedUser = userService.saveUser(user);

        // Then
        assertEquals(user, savedUser);
    }

    @Test
    public void shouldDeleteUserById() {
        // Given
        Long id = 1L;

        // When
        userService.deleteUserById(id);

        // Then
        verify(userRepository, times(1)).deleteById(id);
    }

    @Test
    public void shouldFindUserByEmail() {
        // Given
        String email = "test@example.com";
        User expectedUser = new User();
        when(userRepository.findByEmail(email)).thenReturn(expectedUser);

        // When
        Optional<User> actualUser = userService.findUserByEmail(email);

        // Then
        assertTrue(actualUser.isPresent());
        assertEquals(expectedUser, actualUser.get());
    }

    @Test
    public void shouldReturnEmptyWhenUserByEmailNotFound() {
        // Given
        String email = "test@example.com";
        when(userRepository.findByEmail(email)).thenReturn(null);

        // When
        Optional<User> actualUser = userService.findUserByEmail(email);

        // Then
        assertFalse(actualUser.isPresent());
    }
}
