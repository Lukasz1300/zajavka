package projektZajavka2.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import projektZajavka2.entity.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@ActiveProfiles("test")
//@TestPropertySource(locations = "classpath:application-test.yml")
public class OrderItemRepositoryTest {
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private MenuItemRepository menuItemRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private RestaurantRepository restaurantRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    public void testSaveOrderItem() {

        User user = new User();
        user.setUsername("John Doe");
        user.setEmail("johndoe@example.com");
        user.setPassword("password");
        userRepository.save(user);

        Restaurant restaurant = new Restaurant();
        restaurant.setName("Sample Restaurant");
        restaurant.setAddress("456 Example Avenue");
        restaurant.setPhoneNumber("+123456789");
        restaurant.setEmail("restaurant@example.com");
        restaurant.setDescription("A sample restaurant for testing.");
        restaurant.setCreatedAt(LocalDateTime.now());
        restaurantRepository.save(restaurant);

        Order order = new Order();
        order.setUser(user);
        order.setRestaurant(restaurant);
        order.setOrderStatus("PENDING");
        order.setTotalPrice(new BigDecimal("40.00"));
        order.setDeliveryAddress("123 Sample Street");
        order.setOrderDate(LocalDateTime.now()); // Ustaw datę zamówienia
        orderRepository.save(order);

        MenuItem menuItem = new MenuItem();
        menuItem.setName("Sample Menu Item");
        menuItem.setPrice(new BigDecimal("20.00"));
        menuItem.setRestaurant(restaurant);
        menuItemRepository.save(menuItem);

        OrderItem orderItem = new OrderItem(null, order, menuItem, 2, new BigDecimal("20.00"));
        OrderItem savedOrderItem = orderItemRepository.save(orderItem);

        assertNotNull(savedOrderItem.getId());
        assertEquals(2, savedOrderItem.getQuantity());
        assertEquals(new BigDecimal("20.00"), savedOrderItem.getPrice());
    }
}



