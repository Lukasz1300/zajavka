package projektZajavka2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import projektZajavka2.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    // metody zapytań mogą być tutaj zdefiniowane

    boolean existsByUsername(String username);

    User findByUsername(String username);

    User findByEmail(String email);
}

