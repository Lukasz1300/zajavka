package projektZajavka2.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import projektZajavka2.entity.RestaurantOwner;
import projektZajavka2.service.RestaurantOwnerService;

import java.util.Optional;

@Controller
@RequestMapping("/restaurant-owners")
public class RestaurantOwnerController {

    private final RestaurantOwnerService ownerService;

    @Autowired
    public RestaurantOwnerController(RestaurantOwnerService ownerService) {
        this.ownerService = ownerService;
    }

    @GetMapping("/{id}")
    public String getOwnerById(@PathVariable Long id, Model model, HttpServletResponse response) {
        Optional<RestaurantOwner> owner = ownerService.findOwnerById(id);
        if (owner.isPresent()) {
            model.addAttribute("owner", owner.get());
            return "restaurantowners/details"; // Zwraca widok "details.html"
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND); // Ustawienie statusu 404
            return "error/404"; // Zwraca widok błędu 404
        }
    }
}
