package projektZajavka2.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import projektZajavka2.entity.MenuItem;
import projektZajavka2.entity.Order;
import projektZajavka2.entity.Restaurant;
import projektZajavka2.entity.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class OrderRepositoryTest {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RestaurantRepository restaurantRepository;
    @Autowired
    private MenuItemRepository menuItemRepository;

    private Order order;

    @BeforeEach
    public void setUp() {

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


        MenuItem menuItem = new MenuItem();
        menuItem.setName("Sample Menu Item");
        menuItem.setPrice(new BigDecimal("20.00"));
        menuItem.setRestaurant(restaurant);
        menuItemRepository.save(menuItem);


        order = new Order();
        order.setUser(user);
        order.setRestaurant(restaurant);
        order.setOrderStatus("PENDING");
        order.setTotalPrice(new BigDecimal("40.00"));
        order.setDeliveryAddress("123 Sample Street");
        order.setOrderItems(new HashSet<>());
        orderRepository.save(order);
    }

    @Test
    public void testFindById() {
        Order savedOrder = orderRepository.findById(order.getId()).orElse(null);
        assertThat(savedOrder).isNotNull();
        assertThat(savedOrder.getOrderStatus()).isEqualTo("PENDING");
        assertThat(savedOrder.getTotalPrice()).isEqualByComparingTo(new BigDecimal("40.00"));
        assertThat(savedOrder.getDeliveryAddress()).isEqualTo("123 Sample Street");
    }
}
