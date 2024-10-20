package projektZajavka2.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import projektZajavka2.entity.Order;
import projektZajavka2.service.OrderService;
import projektZajavka2.service.RestaurantService;
import projektZajavka2.service.UserService;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private final UserService userService;
    private final RestaurantService restaurantService;
    private final OrderService orderService;

    @Autowired
    public OrderController(UserService userService, RestaurantService restaurantService, OrderService orderService) {
        this.userService = userService;
        this.restaurantService = restaurantService;
        this.orderService = orderService;
    }

    @GetMapping
    public String getAllOrders(Model model) {
        List<Order> orders = orderService.findAllOrders();
        model.addAttribute("orders", orders);
        return "orders/list";
    }

    @GetMapping("{id}")
    public String getOrderById(@PathVariable Long id, Model model, HttpServletResponse response) {
        Optional<Order> orderOptional = orderService.findOrderById(id);
        if (orderOptional.isPresent()) {
            model.addAttribute("order", orderOptional.get());
            return "orders/view"; // Możesz zwrócić widok w innym formacie, jeśli chcesz.
        } else {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            model.addAttribute("error", "Menu item not found");
            return "error/404";
        }
    }


    @GetMapping("/user/{userId}")
    public String getOrdersByUserId(@PathVariable Long userId, Model model) {
        List<Order> orders = orderService.findOrdersByUserId(userId);
        model.addAttribute("orders", orders);
        return "orders/list";
    }

    @GetMapping("/restaurant/{restaurantId}")
    public String getOrdersByRestaurantId(@PathVariable Long restaurantId, Model model) {
        List<Order> orders = orderService.findOrdersByRestaurantId(restaurantId);
        model.addAttribute("orders", orders);
        return "orders/list";
    }

    @GetMapping("/new")
    public String showNewOrderForm(Model model) {
        model.addAttribute("order", new Order());
        model.addAttribute("users", userService.findAllUsers());
        model.addAttribute("restaurants", restaurantService.findAllRestaurants());
        return "orders/form";
    }

    @PostMapping
    public String saveOrder(@ModelAttribute @Valid Order order, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("order", order);
            return "orders/form";
        }
        orderService.saveOrder(order);
        System.out.println("Saved order: " + order);
        return "redirect:/orders";
    }

    @DeleteMapping("/delete/{id}")
    public String deleteOrder(@PathVariable Long id) {
        orderService.deleteOrderById(id);
        return "redirect:/orders";
    }
}
