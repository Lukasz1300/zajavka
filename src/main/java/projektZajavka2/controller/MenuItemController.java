package projektZajavka2.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import projektZajavka2.entity.MenuItem;
import projektZajavka2.service.MenuItemService;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/menu-item")
public class MenuItemController {

    private final MenuItemService menuItemService;

    @Autowired
    public MenuItemController(MenuItemService menuItemService) {
        this.menuItemService = menuItemService;
    }

    // Pobranie wszystkich pozycji menu
    @GetMapping
    public String getAllMenuItems(Model model) {
        List<MenuItem> menuItems = menuItemService.findAllMenuItems();
        model.addAttribute("menuItems", menuItems);
        return "menuitem/list";  // istnieje widok "list.html" w katalogu "menuitems"
    }

    // Pobranie pozycji menu według identyfikatora restauracji
    @GetMapping("/restaurant/{restaurantId}")
    public String getMenuItemsByRestaurantId(@PathVariable Long restaurantId, Model model) {
        List<MenuItem> menuItems = menuItemService.findMenuItemsByRestaurantId(restaurantId);
        model.addAttribute("menuItems", menuItems);
        return "menuitem/list";
    }

    @GetMapping("/{id}")
    public String getMenuItemById(@PathVariable("id") Long id, Model model, HttpServletResponse response) {
        Optional<MenuItem> menuItemOptional = menuItemService.findMenuItemById(id);
        if (menuItemOptional.isPresent()) {
            model.addAttribute("menuItem", menuItemOptional.get());
            return "menuitem/detail";
        } else {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            model.addAttribute("error", "Menu item not found");
            return "error/404";
        }
    }

    // Formularz dodawania nowej pozycji menu
    @GetMapping("/new")
    public String showNewMenuItemForm(Model model) {
        model.addAttribute("menuItem", new MenuItem());
        return "menuitem/form";  // istnieje widok "form.html" w katalogu "menuitems"
    }

    // Zapisywanie nowej pozycji menu
    @PostMapping
    public String saveMenuItem(@Valid @ModelAttribute MenuItem menuItem, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "menuitem/form"; // Jeśli występują błędy walidacji, wyświetl ponownie formularz
        }
        menuItemService.saveMenuItem(menuItem);
        return "redirect:/menu-item";
    }

    // Usuwanie pozycji menu po ID
    @DeleteMapping("/delete/{id}")
    public String deleteMenuItem(@PathVariable Long id) {
        menuItemService.deleteMenuItemById(id);
        return "redirect:/menu-item";
    }
}
