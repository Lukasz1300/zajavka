package projektZajavka2.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import projektZajavka2.entity.DeliveryArea;
import projektZajavka2.service.DeliveryAreaService;

import java.util.List;

@RestController
@RequestMapping("/api/deliveryareas")
// @AllArgsConstructor
public class DeliveryAreaRestController {

    private final DeliveryAreaService deliveryAreaService;

    @Autowired
    public DeliveryAreaRestController(DeliveryAreaService deliveryAreaService) {
        this.deliveryAreaService = deliveryAreaService;
    }

    // Pobranie wszystkich obszarów dostawy dla danej restauracji
    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<DeliveryArea>> getDeliveryAreasByRestaurantId(@PathVariable Long restaurantId) {
        List<DeliveryArea> deliveryAreas = deliveryAreaService.findByRestaurantId(restaurantId);
        return new ResponseEntity<>(deliveryAreas, HttpStatus.OK);
    }
}







