package projektZajavka2.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import projektZajavka2.entity.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
//@TestPropertySource(locations = "classpath:application-test.yml")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    public void setUp() {

        user1 = new User();
        user1.setUsername("john_doe");
        user1.setPassword("password123");
        user1.setEmail("john.doe@example.com");
        userRepository.save(user1);

        user2 = new User();
        user2.setUsername("tomek_doe");
        user2.setPassword("password123");
        user2.setEmail("tomek.doe@example.com");
        userRepository.save(user2);

        user3 = new User();
        user3.setUsername("alice_smith");
        user3.setPassword("password123");
        user3.setEmail("alice.smith@example.com");
        userRepository.save(user3);

        User testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("testpassword");
        testUser.setEmail("testuser@example.com");
        userRepository.save(testUser);
    }

    @Test
    public void givenUser_whenSaved_thenCanBeFoundByUsername() {
        // When
        User foundUser = userRepository.findByUsername("john_doe");

        // Then
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getUsername()).isEqualTo("john_doe");
        assertThat(foundUser.getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    public void givenUser_whenSaved_thenCanBeFoundByEmail() {
        // When
        User foundUser = userRepository.findByEmail("tomek.doe@example.com");

        // Then
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getUsername()).isEqualTo("tomek_doe");
        assertThat(foundUser.getEmail()).isEqualTo("tomek.doe@example.com");
    }

    @Test
    public void givenUser_whenDeleted_thenShouldNotBeFound() {
        // When
        userRepository.deleteById(user3.getId());
        User foundUser = userRepository.findById(user3.getId()).orElse(null);

        // Then
        assertThat(foundUser).isNull();
    }

    @Test
    public void givenTestUser_whenSaved_thenCanBeFoundByUsername() {
        // When
        User foundUser = userRepository.findByUsername("testuser");

        // Then
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getUsername()).isEqualTo("testuser");
        assertThat(foundUser.getEmail()).isEqualTo("testuser@example.com");
    }
}
