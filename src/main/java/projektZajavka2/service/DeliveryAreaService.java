package projektZajavka2.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import projektZajavka2.entity.DeliveryArea;
import projektZajavka2.repository.DeliveryAreaRepository;

import java.util.List;


@Transactional
@Service
@Slf4j
public class DeliveryAreaService {

    private final DeliveryAreaRepository deliveryAreaRepository;

    @Autowired
    public DeliveryAreaService(DeliveryAreaRepository deliveryAreaRepository) {
        this.deliveryAreaRepository = deliveryAreaRepository;
    }

    // Metoda do znalezienia obszarów dostawy według identyfikatora restauracji
    public List<DeliveryArea> findByRestaurantId(Long restaurantId) {
        return deliveryAreaRepository.findByRestaurantId(restaurantId);
    }
}
