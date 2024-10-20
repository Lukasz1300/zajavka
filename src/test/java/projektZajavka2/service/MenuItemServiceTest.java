package projektZajavka2.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import projektZajavka2.entity.MenuItem;
import projektZajavka2.repository.MenuItemRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class MenuItemServiceTest {

    @Mock
    private MenuItemRepository menuItemRepository;

    @InjectMocks
    private MenuItemService menuItemService;

    @Test
    public void shouldFindAllMenuItems() {
        // Given
        List<MenuItem> expectedMenuItems = Collections.singletonList(new MenuItem());
        when(menuItemRepository.findAll()).thenReturn(expectedMenuItems);

        // When
        List<MenuItem> actualMenuItems = menuItemService.findAllMenuItems();

        // Then
        assertEquals(expectedMenuItems, actualMenuItems);
    }

    @Test
    public void shouldFindMenuItemById() {
        // Given
        Long id = 1L;
        MenuItem expectedMenuItem = new MenuItem();
        when(menuItemRepository.findById(id)).thenReturn(Optional.of(expectedMenuItem));

        // When
        Optional<MenuItem> actualMenuItem = menuItemService.findMenuItemById(id);

        // Then
        assertTrue(actualMenuItem.isPresent());
        assertEquals(expectedMenuItem, actualMenuItem.get());
    }

    @Test
    public void shouldFindMenuItemsByRestaurantId() {
        // Given
        Long restaurantId = 1L;
        List<MenuItem> expectedMenuItems = Collections.singletonList(new MenuItem());
        when(menuItemRepository.findByRestaurantId(restaurantId)).thenReturn(expectedMenuItems);

        // When
        List<MenuItem> actualMenuItems = menuItemService.findMenuItemsByRestaurantId(restaurantId);

        // Then
        assertEquals(expectedMenuItems, actualMenuItems);
    }

    @Test
    public void shouldSaveMenuItem() {
        // Given
        MenuItem menuItem = new MenuItem();
        when(menuItemRepository.save(menuItem)).thenReturn(menuItem);

        // When
        MenuItem savedMenuItem = menuItemService.saveMenuItem(menuItem);

        // Then
        assertEquals(menuItem, savedMenuItem);
    }

    @Test
    public void shouldDeleteMenuItemById() {
        // Given
        Long id = 1L;
        doNothing().when(menuItemRepository).deleteById(id);

        // When
        menuItemService.deleteMenuItemById(id);

        // Then
        verify(menuItemRepository, times(1)).deleteById(id);
    }
}
