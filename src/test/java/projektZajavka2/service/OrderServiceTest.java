package projektZajavka2.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import projektZajavka2.entity.Order;
import projektZajavka2.repository.OrderRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    public void shouldFindAllOrders() {
        // Given
        List<Order> expectedOrders = Collections.singletonList(new Order());
        when(orderRepository.findAll()).thenReturn(expectedOrders);

        // When
        List<Order> actualOrders = orderService.findAllOrders();

        // Then
        assertEquals(expectedOrders, actualOrders);
    }

    @Test
    public void shouldFindOrderById() {
        // Given
        Long id = 1L;
        Order expectedOrder = new Order();
        when(orderRepository.findById(id)).thenReturn(Optional.of(expectedOrder));

        // When
        Optional<Order> actualOrder = orderService.findOrderById(id);

        // Then
        assertTrue(actualOrder.isPresent());
        assertEquals(expectedOrder, actualOrder.get());
    }

    @Test
    public void shouldFindOrdersByUserId() {
        // Given
        Long userId = 1L;
        List<Order> expectedOrders = Collections.singletonList(new Order());
        when(orderRepository.findByUserId(userId)).thenReturn(expectedOrders);

        // When
        List<Order> actualOrders = orderService.findOrdersByUserId(userId);

        // Then
        assertEquals(expectedOrders, actualOrders);
    }

    @Test
    public void shouldFindOrdersByRestaurantId() {
        // Given
        Long restaurantId = 1L;
        List<Order> expectedOrders = Collections.singletonList(new Order());
        when(orderRepository.findByRestaurantId(restaurantId)).thenReturn(expectedOrders);

        // When
        List<Order> actualOrders = orderService.findOrdersByRestaurantId(restaurantId);

        // Then
        assertEquals(expectedOrders, actualOrders);
    }

    @Test
    public void shouldSaveOrder() {
        // Given
        Order order = new Order();
        when(orderRepository.save(order)).thenReturn(order);

        // When
        Order savedOrder = orderService.saveOrder(order);

        // Then
        assertEquals(order, savedOrder);
    }

    @Test
    public void shouldDeleteOrderById() {
        // Given
        Long id = 1L;
        doNothing().when(orderRepository).deleteById(id);

        // When
        orderService.deleteOrderById(id);

        // Then
        verify(orderRepository, times(1)).deleteById(id);
    }
}
