package projektZajavka2.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import projektZajavka2.entity.DeliveryArea;
import projektZajavka2.repository.DeliveryAreaRepository;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeliveryAreaServiceTest {

    @Mock
    private DeliveryAreaRepository deliveryAreaRepository;

    @InjectMocks
    private DeliveryAreaService deliveryAreaService;

    @Test
    public void testFindByRestaurantId() {
        // Given
        Long restaurantId = 1L;
        DeliveryArea area1 = new DeliveryArea();
        DeliveryArea area2 = new DeliveryArea();
        List<DeliveryArea> deliveryAreas = Arrays.asList(area1, area2);

        when(deliveryAreaRepository.findByRestaurantId(restaurantId)).thenReturn(deliveryAreas);

        // When
        List<DeliveryArea> result = deliveryAreaService.findByRestaurantId(restaurantId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(area1, area2);
        verify(deliveryAreaRepository, times(1)).findByRestaurantId(restaurantId);
    }
}
