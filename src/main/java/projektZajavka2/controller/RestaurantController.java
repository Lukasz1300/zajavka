package projektZajavka2.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import projektZajavka2.entity.Restaurant;
import projektZajavka2.service.RestaurantService;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;

    @Autowired
    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    // Pobranie wszystkich restauracji
    @GetMapping
    public String getAllRestaurants(Model model) {
        List<Restaurant> restaurants = restaurantService.findAllRestaurants();
        model.addAttribute("restaurants", restaurants);
        return "restaurants/list"; // widok "list.html" w katalogu "restaurants"
    }

    // Pobranie restauracji według ID
    @GetMapping("/{id}")
    public String getRestaurantById(@PathVariable Long id, Model model, HttpServletResponse response) {
        Optional<Restaurant> restaurant = restaurantService.findRestaurantById(id);
        if (restaurant.isPresent()) {
            model.addAttribute("restaurant", restaurant.get());
            return "restaurants/view"; // Widok, gdy restauracja istnieje
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND); // Ustawienie statusu 404
            model.addAttribute("message", "Restaurant not found");
            return "error/404"; // Zwrócenie widoku błędu 404
        }
    }

    // Pobranie restauracji według ID właściciela
    @GetMapping("/owner/{ownerId}")
    public String getRestaurantsByOwnerId(@PathVariable Long ownerId, Model model) {
        List<Restaurant> restaurants = restaurantService.findRestaurantsByOwnerId(ownerId);
        model.addAttribute("restaurants", restaurants);
        return "restaurants/list"; // Można użyć tego samego widoku listy do wyświetlania restauracji właściciela
    }

    // Formularz do tworzenia nowej restauracji
    @GetMapping("/new")
    public String showNewRestaurantForm(Model model) {
        model.addAttribute("restaurant", new Restaurant());
        return "restaurants/form"; // istnieje widok "form.html"
    }

    // Zapisywanie restauracji
    @PostMapping
    public String saveRestaurant(@Valid @ModelAttribute Restaurant restaurant, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            // Jeśli są błędy walidacji, wróć do formularza
            return "restaurants/form";
        }
        restaurantService.saveRestaurant(restaurant);
        return "redirect:/restaurants";
    }

    // Usuwanie restauracji według ID
    @DeleteMapping("/delete/{id}")
    public String deleteRestaurant(@PathVariable Long id) {
        restaurantService.deleteRestaurantById(id);
        return "redirect:/restaurants";
    }
}

