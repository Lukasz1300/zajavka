package projektZajavka2.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import projektZajavka2.entity.OrderItem;
import projektZajavka2.repository.OrderItemRepository;

import java.util.List;
import java.util.Optional;

@Transactional
@Service
@Slf4j
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;

    @Autowired
    public OrderItemService(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }

    public List<OrderItem> findAllOrderItems() {
        return orderItemRepository.findAll();
    }

    public Optional<OrderItem> findOrderItemById(Long id) {
        return orderItemRepository.findById(id);
    }

    public List<OrderItem> findOrderItemsByOrderId(Long orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }

    public List<OrderItem> findOrderItemsByMenuItemId(Long menuItemId) {
        return orderItemRepository.findByMenuItemId(menuItemId);
    }

    public OrderItem saveOrderItem(OrderItem orderItem) {
        return orderItemRepository.save(orderItem);
    }

    public void deleteOrderItemById(Long id) {
        orderItemRepository.deleteById(id);
    }
}

