package projektZajavka2.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import projektZajavka2.entity.RestaurantOwner;
import projektZajavka2.repository.RestaurantOwnerRepository;

import java.util.Optional;

@Transactional
@Service
@AllArgsConstructor
@Slf4j
public class RestaurantOwnerService {

    private final RestaurantOwnerRepository ownerRepository;

    public Optional<RestaurantOwner> findOwnerById(Long id) {
        return ownerRepository.findById(id);
    }

}
