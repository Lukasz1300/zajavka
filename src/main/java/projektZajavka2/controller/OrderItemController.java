package projektZajavka2.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import projektZajavka2.entity.OrderItem;
import projektZajavka2.service.OrderItemService;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/order-items")
public class OrderItemController {

    private final OrderItemService orderItemService;

    @Autowired
    public OrderItemController(OrderItemService orderItemService) {
        this.orderItemService = orderItemService;
    }

    // Pobranie wszystkich pozycji zamówień
    @GetMapping
    public String getAllOrderItems(Model model) {
        List<OrderItem> orderItems = orderItemService.findAllOrderItems();
        model.addAttribute("orderItems", orderItems);
        return "orderitems/list"; // istnieje widok "list.html" w katalogu "orderitems"
    }

    // Formularz do tworzenia nowego elementu zamówienia
    @GetMapping("/new")
    public String showNewOrderItemForm(Model model) {
        model.addAttribute("orderItem", new OrderItem());
        return "orderitems/form"; // istnieje widok "form.html" do tworzenia lub edycji pozycji zamówienia
    }

    // Zapisywanie nowego elementu zamówienia
    @PostMapping
    public String saveOrderItem(@ModelAttribute @Valid OrderItem orderItem, BindingResult result, Model model) {
        if (result.hasErrors()) {
            // Jeśli wystąpiły błędy walidacji, wróć do formularza z błędami
            model.addAttribute("orderItem", orderItem);
            return "orderitems/form"; // istnieje widok "form.html" z formularzem
        }
        orderItemService.saveOrderItem(orderItem);
        return "redirect:/order-items";
    }

    // Pobranie pozycji zamówienia według ID
    @GetMapping("/{id}")
    public String getOrderItemById(@PathVariable Long id, Model model, HttpServletResponse response) {
        Optional<OrderItem> orderItemOptional = orderItemService.findOrderItemById(id);
        if (orderItemOptional.isPresent()) {
            model.addAttribute("orderItem", orderItemOptional.get());
            return "orderitems/view";
        } else {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            model.addAttribute("error", "Menu item not found");
            return "error/404";
        }
    }

    // Usuwanie pozycji zamówienia według ID
    @DeleteMapping("/delete/{id}")
    public String deleteOrderItem(@PathVariable Long id) {
        orderItemService.deleteOrderItemById(id);
        return "redirect:/order-items";
    }
}
