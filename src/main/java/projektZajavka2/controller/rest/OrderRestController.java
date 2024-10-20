package projektZajavka2.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import projektZajavka2.entity.Order;
import projektZajavka2.service.OrderService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
public class OrderRestController {

    private final OrderService orderService;

    @Autowired
    public OrderRestController(OrderService orderService) {
        this.orderService = orderService;
    }

    // Pobranie wszystkich zamówień
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.findAllOrders();
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    // Pobranie zamówienia według ID
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        Optional<Order> order = orderService.findOrderById(id);
        return order.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Pobranie zamówień według ID użytkownika
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getOrdersByUserId(@PathVariable Long userId) {
        List<Order> orders = orderService.findOrdersByUserId(userId);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    // Pobranie zamówień według ID restauracji
    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<Order>> getOrdersByRestaurantId(@PathVariable Long restaurantId) {
        List<Order> orders = orderService.findOrdersByRestaurantId(restaurantId);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    // Dodanie nowego zamówienia
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        Order savedOrder = orderService.saveOrder(order);
        return new ResponseEntity<>(savedOrder, HttpStatus.CREATED);
    }

    // Aktualizacja zamówienia
    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable Long id, @RequestBody Order order) {
        Optional<Order> existingOrder = orderService.findOrderById(id);
        if (existingOrder.isPresent()) {
            order.setId(id);
            Order updatedOrder = orderService.saveOrder(order);
            return new ResponseEntity<>(updatedOrder, HttpStatus.OK);
        }
        return ResponseEntity.notFound().build();
    }

    // Usuwanie zamówienia
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        Optional<Order> existingOrder = orderService.findOrderById(id);
        if (existingOrder.isPresent()) {
            orderService.deleteOrderById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
