package projektZajavka2.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import projektZajavka2.entity.OrderItem;
import projektZajavka2.repository.OrderItemRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class OrderItemServiceTest {

    @Mock
    private OrderItemRepository orderItemRepository;

    @InjectMocks
    private OrderItemService orderItemService;

    @Test
    public void shouldFindAllOrderItems() {
        // Given
        List<OrderItem> expectedOrderItems = Collections.singletonList(new OrderItem());
        when(orderItemRepository.findAll()).thenReturn(expectedOrderItems);

        // When
        List<OrderItem> actualOrderItems = orderItemService.findAllOrderItems();

        // Then
        assertEquals(expectedOrderItems, actualOrderItems);
    }

    @Test
    public void shouldFindOrderItemById() {
        // Given
        Long id = 1L;
        OrderItem expectedOrderItem = new OrderItem();
        when(orderItemRepository.findById(id)).thenReturn(Optional.of(expectedOrderItem));

        // When
        Optional<OrderItem> actualOrderItem = orderItemService.findOrderItemById(id);

        // Then
        assertTrue(actualOrderItem.isPresent());
        assertEquals(expectedOrderItem, actualOrderItem.get());
    }

    @Test
    public void shouldFindOrderItemsByOrderId() {
        // Given
        Long orderId = 1L;
        List<OrderItem> expectedOrderItems = Collections.singletonList(new OrderItem());
        when(orderItemRepository.findByOrderId(orderId)).thenReturn(expectedOrderItems);

        // When
        List<OrderItem> actualOrderItems = orderItemService.findOrderItemsByOrderId(orderId);

        // Then
        assertEquals(expectedOrderItems, actualOrderItems);
    }

    @Test
    public void shouldFindOrderItemsByMenuItemId() {
        // Given
        Long menuItemId = 1L;
        List<OrderItem> expectedOrderItems = Collections.singletonList(new OrderItem());
        when(orderItemRepository.findByMenuItemId(menuItemId)).thenReturn(expectedOrderItems);

        // When
        List<OrderItem> actualOrderItems = orderItemService.findOrderItemsByMenuItemId(menuItemId);

        // Then
        assertEquals(expectedOrderItems, actualOrderItems);
    }

    @Test
    public void shouldSaveOrderItem() {
        // Given
        OrderItem orderItem = new OrderItem();
        when(orderItemRepository.save(orderItem)).thenReturn(orderItem);

        // When
        OrderItem savedOrderItem = orderItemService.saveOrderItem(orderItem);

        // Then
        assertEquals(orderItem, savedOrderItem);
    }

    @Test
    public void shouldDeleteOrderItemById() {
        // Given
        Long id = 1L;
        doNothing().when(orderItemRepository).deleteById(id);

        // When
        orderItemService.deleteOrderItemById(id);

        // Then
        verify(orderItemRepository, times(1)).deleteById(id);
    }
}
