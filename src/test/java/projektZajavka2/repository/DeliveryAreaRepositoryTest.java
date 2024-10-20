package projektZajavka2.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import projektZajavka2.entity.DeliveryArea;
import projektZajavka2.entity.Restaurant;
import projektZajavka2.entity.RestaurantOwner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class DeliveryAreaRepositoryTest {
    @Autowired
    private DeliveryAreaRepository deliveryAreaRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private RestaurantOwnerRepository restaurantOwnerRepository;

    @Test
    public void givenDeliveryAreas_whenFindByRestaurantId_thenReturnDeliveryAreas() {

        RestaurantOwner owner1 = new RestaurantOwner();
        owner1.setName("Owner 1");
        owner1.setEmail("owner1@example.com");
        owner1.setPhoneNumber("+123456789");
        restaurantOwnerRepository.save(owner1);

        Restaurant restaurant2 = new Restaurant();
        restaurant2.setOwner(owner1);
        restaurant2.setName("Restaurant 2");
        restaurant2.setAddress("456 Elm St");
        restaurant2.setPhoneNumber("+123456789");
        restaurant2.setEmail("restaurant2@example.com");
        restaurant2.setDescription("Description of Restaurant 2");
        restaurantRepository.save(restaurant2);

        DeliveryArea area3 = new DeliveryArea();
        area3.setRestaurant(restaurant2);
        area3.setStreetName("Street 3");
        deliveryAreaRepository.save(area3);

        List<DeliveryArea> areas = deliveryAreaRepository.findByRestaurantId(restaurant2.getId());

        // Walidacja
        assertThat(areas).hasSize(1);  // Powinno zwrócić 1 obszar dostawy, nie 2
        assertThat(areas).extracting(DeliveryArea::getStreetName)
                .containsExactly("Street 3");  // Sprawdzamy, czy nazwa ulicy jest poprawna
        assertThat(areas).extracting(area -> area.getRestaurant().getId())
                .containsOnly(restaurant2.getId());  // Upewniamy się, że obszar dostawy należy do restauracji 2
    }
}