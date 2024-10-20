package projektZajavka2.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import projektZajavka2.entity.MenuItem;
import projektZajavka2.service.MenuItemService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/menu-items")
public class MenuItemRestController {

    private final MenuItemService menuItemService;

    @Autowired
    public MenuItemRestController(MenuItemService menuItemService) {
        this.menuItemService = menuItemService;
    }

    // Pobranie wszystkich pozycji menu
    @GetMapping
    public ResponseEntity<List<MenuItem>> getAllMenuItems() {
        List<MenuItem> menuItems = menuItemService.findAllMenuItems();
        return new ResponseEntity<>(menuItems, HttpStatus.OK);
    }

    // Pobranie pozycji menu według identyfikatora restauracji
    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<MenuItem>> getMenuItemsByRestaurantId(@PathVariable Long restaurantId) {
        List<MenuItem> menuItems = menuItemService.findMenuItemsByRestaurantId(restaurantId);
        return new ResponseEntity<>(menuItems, HttpStatus.OK);
    }

    // Pobranie pozycji menu według identyfikatora
    @GetMapping("/{id}")
    public ResponseEntity<MenuItem> getMenuItemById(@PathVariable Long id) {
        Optional<MenuItem> menuItemOptional = menuItemService.findMenuItemById(id);
        return menuItemOptional.map(menuItem -> new ResponseEntity<>(menuItem, HttpStatus.OK)).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Dodanie nowej pozycji menu
    @PostMapping
    public ResponseEntity<MenuItem> createMenuItem(@RequestBody MenuItem menuItem) {
        MenuItem savedMenuItem = menuItemService.saveMenuItem(menuItem);
        return new ResponseEntity<>(savedMenuItem, HttpStatus.CREATED);
    }

    // Aktualizacja pozycji menu
    @PutMapping("/{id}")
    public ResponseEntity<MenuItem> updateMenuItem(@PathVariable Long id, @RequestBody MenuItem menuItem) {
        Optional<MenuItem> existingMenuItem = menuItemService.findMenuItemById(id);
        if (existingMenuItem.isPresent()) {
            menuItem.setId(id);
            MenuItem updatedMenuItem = menuItemService.saveMenuItem(menuItem);
            return new ResponseEntity<>(updatedMenuItem, HttpStatus.OK);
        }
        return ResponseEntity.notFound().build();
    }

    // Usuwanie pozycji menu
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable Long id) {
        Optional<MenuItem> existingMenuItem = menuItemService.findMenuItemById(id);
        if (existingMenuItem.isPresent()) {
            menuItemService.deleteMenuItemById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
