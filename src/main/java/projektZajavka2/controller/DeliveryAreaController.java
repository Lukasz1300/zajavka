package projektZajavka2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import projektZajavka2.entity.DeliveryArea;
import projektZajavka2.service.DeliveryAreaService;

import java.util.List;

@Controller
@RequestMapping("/deliveryareas")
public class DeliveryAreaController {

    private final DeliveryAreaService deliveryAreaService;

    @Autowired
    public DeliveryAreaController(DeliveryAreaService deliveryAreaService) {
        this.deliveryAreaService = deliveryAreaService;
    }

    // Pobranie wszystkich obszarów dostawy dla danej restauracji
    @GetMapping("/restaurant/{restaurantId}")
    public String getDeliveryAreasByRestaurantId(@PathVariable Long restaurantId, Model model) {
        List<DeliveryArea> deliveryAreas = deliveryAreaService.findByRestaurantId(restaurantId);
        model.addAttribute("deliveryAreas", deliveryAreas);
        return "/deliveryareas/list";
    }
}
