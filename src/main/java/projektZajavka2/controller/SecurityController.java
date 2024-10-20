package projektZajavka2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SecurityController {

    @GetMapping("/security/login")
    public String login() {
        return "security/login"; // Zwraca widok HTML dla logowania
    }
}
