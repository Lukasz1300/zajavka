package projektZajavka2.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import projektZajavka2.entity.RestaurantOwner;
import projektZajavka2.repository.RestaurantOwnerRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class RestaurantOwnerServiceTest {

    @Mock
    private RestaurantOwnerRepository ownerRepository;

    @InjectMocks
    private RestaurantOwnerService ownerService;

    @Test
    public void shouldFindOwnerById() {
        // Given
        Long id = 1L;
        RestaurantOwner expectedOwner = new RestaurantOwner();
        when(ownerRepository.findById(id)).thenReturn(Optional.of(expectedOwner));

        // When
        Optional<RestaurantOwner> actualOwner = ownerService.findOwnerById(id);

        // Then
        assertTrue(actualOwner.isPresent());
        assertEquals(expectedOwner, actualOwner.get());
    }
}
