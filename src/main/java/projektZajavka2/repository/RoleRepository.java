package projektZajavka2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import projektZajavka2.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByName(String name);
}
