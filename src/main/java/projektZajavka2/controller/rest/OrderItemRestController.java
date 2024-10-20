package projektZajavka2.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import projektZajavka2.entity.OrderItem;
import projektZajavka2.service.OrderItemService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/order-items")
public class OrderItemRestController {

    private final OrderItemService orderItemService;

    @Autowired
    public OrderItemRestController(OrderItemService orderItemService) {
        this.orderItemService = orderItemService;
    }

    // Pobranie wszystkich pozycji zamówień
    @GetMapping
    public ResponseEntity<List<OrderItem>> getAllOrderItems() {
        List<OrderItem> orderItems = orderItemService.findAllOrderItems();
        return new ResponseEntity<>(orderItems, HttpStatus.OK);
    }

    // Pobranie pozycji zamówienia według ID
    @GetMapping("/{id}")
    public ResponseEntity<OrderItem> getOrderItemById(@PathVariable Long id) {
        Optional<OrderItem> orderItem = orderItemService.findOrderItemById(id);
        return orderItem.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Pobranie pozycji zamówień według ID zamówienia
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<OrderItem>> getOrderItemsByOrderId(@PathVariable Long orderId) {
        List<OrderItem> orderItems = orderItemService.findOrderItemsByOrderId(orderId);
        return new ResponseEntity<>(orderItems, HttpStatus.OK);
    }

    // Pobranie pozycji zamówień według ID pozycji menu
    @GetMapping("/menu-item/{menuItemId}")
    public ResponseEntity<List<OrderItem>> getOrderItemsByMenuItemId(@PathVariable Long menuItemId) {
        List<OrderItem> orderItems = orderItemService.findOrderItemsByMenuItemId(menuItemId);
        return new ResponseEntity<>(orderItems, HttpStatus.OK);
    }

    // Dodanie nowego elementu zamówienia
    @PostMapping
    public ResponseEntity<OrderItem> createOrderItem(@RequestBody OrderItem orderItem) {
        OrderItem savedOrderItem = orderItemService.saveOrderItem(orderItem);
        return new ResponseEntity<>(savedOrderItem, HttpStatus.CREATED);
    }

    // Aktualizacja pozycji zamówienia
    @PutMapping("/{id}")
    public ResponseEntity<OrderItem> updateOrderItem(@PathVariable Long id, @RequestBody OrderItem orderItem) {
        Optional<OrderItem> existingOrderItem = orderItemService.findOrderItemById(id);
        if (existingOrderItem.isPresent()) {
            orderItem.setId(id);
            OrderItem updatedOrderItem = orderItemService.saveOrderItem(orderItem);
            return new ResponseEntity<>(updatedOrderItem, HttpStatus.OK);
        }
        return ResponseEntity.notFound().build();
    }

    // Usuwanie pozycji zamówienia
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrderItem(@PathVariable Long id) {
        Optional<OrderItem> existingOrderItem = orderItemService.findOrderItemById(id);
        if (existingOrderItem.isPresent()) {
            orderItemService.deleteOrderItemById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
