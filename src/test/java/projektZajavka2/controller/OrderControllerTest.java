package projektZajavka2.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import projektZajavka2.entity.Order;
import projektZajavka2.service.OrderService;
import projektZajavka2.service.RestaurantService;
import projektZajavka2.service.UserService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(OrderController.class)
@ActiveProfiles("test")
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private OrderService orderService;

    @MockBean
    private RestaurantService restaurantService;

    @Test
    @WithMockUser
    public void shouldReturnAllOrders() throws Exception {
        List<Order> orders = Collections.singletonList(new Order());
        when(orderService.findAllOrders()).thenReturn(orders);

        mockMvc.perform(MockMvcRequestBuilders.get("/orders"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("orders/list"))
                .andExpect(MockMvcResultMatchers.model().attribute("orders", orders))
                .andDo(print());
    }


    @Test
    @WithMockUser
    public void shouldReturnOrderById() throws Exception {
        Long id = 1L;
        Order order = new Order();
        when(orderService.findOrderById(id)).thenReturn(Optional.of(order));

        mockMvc.perform(MockMvcRequestBuilders.get("/orders/" + id))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("orders/view"))
                .andExpect(MockMvcResultMatchers.model().attribute("order", order))
                .andDo(print());
    }

    @Test
    @WithMockUser
    public void shouldReturn404WhenOrderNotFound() throws Exception {
        Long id = 1L;
        when(orderService.findOrderById(id)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/orders/" + id))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.view().name("error/404"))
                .andDo(print());
    }

    @Test
    @WithMockUser
    public void shouldReturnOrdersByUserId() throws Exception {
        Long userId = 1L;
        List<Order> orders = Collections.singletonList(new Order());
        when(orderService.findOrdersByUserId(userId)).thenReturn(orders);

        mockMvc.perform(MockMvcRequestBuilders.get("/orders/user/" + userId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("orders/list"))
                .andExpect(MockMvcResultMatchers.model().attribute("orders", orders))
                .andDo(print());
    }

    @Test
    @WithMockUser
    public void shouldReturnOrdersByRestaurantId() throws Exception {
        Long restaurantId = 1L;
        List<Order> orders = Collections.singletonList(new Order());
        when(orderService.findOrdersByRestaurantId(restaurantId)).thenReturn(orders);

        mockMvc.perform(MockMvcRequestBuilders.get("/orders/restaurant/" + restaurantId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("orders/list"))
                .andExpect(MockMvcResultMatchers.model().attribute("orders", orders))
                .andDo(print());
    }

    @Test
    @WithMockUser
    public void shouldShowNewOrderForm() throws Exception {
        when(userService.findAllUsers()).thenReturn(Collections.emptyList());
        when(restaurantService.findAllRestaurants()).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/orders/new"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("orders/form"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("order"))
                .andDo(print());
    }

    @Test
    @WithMockUser
    public void shouldSaveOrder() throws Exception {
        Order order = new Order();
        mockMvc.perform(MockMvcRequestBuilders.post("/orders")
                        //ok   .with(csrf())  // Dodanie tokena CSRF
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .flashAttr("order", order))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/orders"))
                .andDo(print());
    }

    @Test
    @WithMockUser
    public void shouldDeleteOrder() throws Exception {
        Long id = 1L;
        mockMvc.perform(MockMvcRequestBuilders.delete("/orders/delete/" + id)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())) // Dodanie tokena CSRF
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/orders"))
                .andDo(print());
    }
}
