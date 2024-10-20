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
import projektZajavka2.entity.OrderItem;
import projektZajavka2.service.OrderItemService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
@ActiveProfiles("test")
@WebMvcTest(OrderItemController.class) // Testowanie tylko kontrolera OrderItemController
public class OrderItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderItemService orderItemService;

    @Test
    @WithMockUser
    public void shouldReturnAllOrderItems() throws Exception {
        // Given
        List<OrderItem> orderItems = Collections.singletonList(new OrderItem());
        when(orderItemService.findAllOrderItems()).thenReturn(orderItems);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/order-items"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("orderitems/list"))
                .andExpect(MockMvcResultMatchers.model().attribute("orderItems", orderItems));
    }

    @Test
    @WithMockUser
    public void shouldShowNewOrderItemForm() throws Exception {
        // Given, When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/order-items/new"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("orderitems/form"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("orderItem"));
    }

    @Test
    @WithMockUser
    public void shouldSaveOrderItem() throws Exception {
        // Given
        OrderItem orderItem = new OrderItem();
        when(orderItemService.saveOrderItem(orderItem)).thenReturn(orderItem);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/order-items")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .flashAttr("orderItem", orderItem))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/order-items"));
    }

    @Test
    @WithMockUser
    public void shouldReturnOrderItemById() throws Exception {
        // Given
        Long id = 1L;
        OrderItem orderItem = new OrderItem();
        when(orderItemService.findOrderItemById(id)).thenReturn(Optional.of(orderItem));

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/order-items/" + id))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("orderitems/view"))
                .andExpect(MockMvcResultMatchers.model().attribute("orderItem", orderItem))
                .andDo(print());
    }

    @Test
    @WithMockUser
    public void shouldReturnNotFoundWhenOrderItemDoesNotExist() throws Exception {
        // Given
        Long id = 1L;
        when(orderItemService.findOrderItemById(id)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/order-items/" + id))
                .andExpect(MockMvcResultMatchers.status().isNotFound()) // Poprawka statusu na 404
                .andExpect(MockMvcResultMatchers.view().name("error/404"))
                .andDo(print());
    }

    @Test
    @WithMockUser
    public void shouldDeleteOrderItemById() throws Exception {
        // Given
        Long id = 1L;

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.delete("/order-items/delete/" + id)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/order-items"));
    }
}
