package projektZajavka2.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import projektZajavka2.entity.MenuItem;
import projektZajavka2.service.MenuItemService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(MenuItemController.class)
@WithMockUser(username = "testuser", roles = {"ADMIN"})
@ActiveProfiles("test")
public class MenuItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MenuItemService menuItemService;

    @Test
    public void shouldReturnAllMenuItems() throws Exception {
        // Given
        List<MenuItem> menuItems = Collections.singletonList(new MenuItem());
        when(menuItemService.findAllMenuItems()).thenReturn(menuItems);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/menu-item"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("menuitem/list"))
                .andExpect(MockMvcResultMatchers.model().attribute("menuItems", menuItems))  // Zmieniamy na "menuItems"
                .andDo(print());
    }

    @Test
    public void shouldReturnMenuItemsByRestaurantId() throws Exception {
        // Given
        Long restaurantId = 1L;
        List<MenuItem> menuItems = Collections.singletonList(new MenuItem());
        when(menuItemService.findMenuItemsByRestaurantId(restaurantId)).thenReturn(menuItems);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/menu-item/restaurant/" + restaurantId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("menuitem/list"))
                .andExpect(MockMvcResultMatchers.model().attribute("menuItems", menuItems))
                .andDo(print());
    }

    @Test
    public void shouldReturnMenuItemById() throws Exception {
        // Given
        Long id = 1L;
        MenuItem menuItem = new MenuItem();
        when(menuItemService.findMenuItemById(id)).thenReturn(Optional.of(menuItem));

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/menu-item/" + id))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("menuitem/detail"))
                .andExpect(MockMvcResultMatchers.model().attribute("menuItem", menuItem))
                .andDo(print());
    }

    @Test
    public void shouldReturn404WhenMenuItemNotFound() throws Exception {
        // Given
        Long id = 1L;
        when(menuItemService.findMenuItemById(id)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/menu-item/" + id))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.view().name("error/404"))
                .andDo(print());
    }
}

