package projektZajavka2.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/templates/error")
    public String handleError() {
        // Zwróć widok "error/404.html" w przypadku błędu
        return "templates/error/404";
    }
}
