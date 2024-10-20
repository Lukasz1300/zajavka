package projektZajavka2.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import projektZajavka2.entity.*;
import projektZajavka2.repository.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@AutoConfigureMockMvc
@WithMockUser(username = "testuser", roles = {"ADMIN"})
@ActiveProfiles("test")
public class OrderItemControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private UserRepository userRepository;

    private Restaurant restaurant;

    @BeforeEach
    public void setUp() {

        restaurant = new Restaurant();
        restaurant.setName("Test Restaurant");
        restaurant.setAddress("123 Test Street");
        restaurant.setPhoneNumber("+123456789");
        restaurant.setEmail("test@restaurant.com");
        restaurantRepository.save(restaurant);  // Zapisujemy do repozytorium, aby utworzyć w bazie
        System.out.println(restaurant);

        User user = new User();
        user.setUsername("jane_doe");
        user.setPassword("password123");
        user.setEmail("jane.doe@example.com");
        userRepository.save(user);
        System.out.println("Saved user: " + user);

        // Setup test data
        Order order = new Order();
        order.setUser(user); // Przypisz użytkownika do zamówienia
        order.setRestaurant(restaurant);
        order.setOrderStatus("PENDING");
        order.setTotalPrice(BigDecimal.valueOf(100.00));
        order.setDeliveryAddress("123 Test Street");
        orderRepository.save(order);
        System.out.println(order);

        // Inicjalizacja pozycji w menu
        MenuItem menuItem = new MenuItem();
        menuItem.setName("Test Menu Item");
        menuItem.setPrice(BigDecimal.valueOf(30.00));
        menuItem.setRestaurant(restaurant); // Przypisz restaurację do pozycji w menu
        menuItemRepository.save(menuItem);
        System.out.println(menuItem);

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setMenuItem(menuItem);
        orderItem.setQuantity(2);
        orderItem.setPrice(BigDecimal.valueOf(20.00));
        orderItemRepository.save(orderItem);
        System.out.println(orderItem);
    }

    @Test
    public void testGetAllOrderItems() throws Exception {
        mockMvc.perform(get("/order-items"))
                .andExpect(status().isOk())
                .andExpect(view().name("orderitems/list"))
                .andExpect(model().attributeExists("orderItems"))
                .andExpect(model().attribute("orderItems", hasSize(1)))
                .andExpect(model().attribute("orderItems", hasItem(
                        allOf(
                                hasProperty("quantity", is(2)),
                                hasProperty("price", is(BigDecimal.valueOf(20.00)))
                        )
                )));
    }

    @Test
    public void testGetOrderItemById() throws Exception {
        OrderItem orderItem = orderItemRepository.findAll().get(0);

        mockMvc.perform(get("/order-items/{id}", orderItem.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("orderitems/view"))
                .andExpect(model().attributeExists("orderItem"))
                .andExpect(model().attribute("orderItem", hasProperty("quantity", is(2))));
    }

    @Test
    public void testShowNewOrderItemForm() throws Exception {
        mockMvc.perform(get("/order-items/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("orderitems/form"))
                .andExpect(model().attributeExists("orderItem"));
    }

    @Test
    public void testSaveOrderItem() throws Exception {
        Order order = orderRepository.findAll().get(0);
        MenuItem menuItem = menuItemRepository.findAll().get(0);

        mockMvc.perform(post("/order-items")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("order.id", order.getId().toString())
                        .param("menuItem.id", menuItem.getId().toString())
                        .param("quantity", "3")
                        .param("price", "30.00"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/order-items"));

        // Verify the new order item is saved
        OrderItem savedOrderItem = orderItemRepository.findAll().get(1); // Index 1 because we have 1 existing item
        assertEquals(3, savedOrderItem.getQuantity());
        assertEquals(0, menuItem.getPrice().compareTo(BigDecimal.valueOf(30.00)));

    }

    @Test
    public void testDeleteOrderItem() throws Exception {
        OrderItem orderItem = orderItemRepository.findAll().get(0);

        // Wykonaj żądanie DELETE
        mockMvc.perform(delete("/order-items/delete/{id}", orderItem.getId())
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/order-items"));
        // Sprawdź, czy element zamówienia został usunięty
        assertEquals(0, orderItemRepository.count());
    }
}
