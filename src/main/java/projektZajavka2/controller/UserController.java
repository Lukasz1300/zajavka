package projektZajavka2.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import projektZajavka2.entity.User;
import projektZajavka2.service.UserService;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Pobranie wszystkich użytkowników
    @GetMapping
    public String getAllUsers(Model model) {
        List<User> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "user/list";  // Widok "list.html" w katalogu "users"
    }

    // Pobranie użytkownika po ID
    @GetMapping("/{id}")
    public String getUserById(@PathVariable Long id, Model model, HttpServletResponse response) {
        Optional<User> user = userService.findUserById(id);
        if (user.isPresent()) {
            model.addAttribute("user", user.get());
            return "user/detail";  // Widok "detail.html" w katalogu "users"
        } else {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            model.addAttribute("error", "User Id not found");
            return "error/404";  // Widok strony błędu 404
        }
    }

    // Pobranie użytkownika po nazwie użytkownika
    @GetMapping("/username/{username}")
    public String getUserByUsername(@PathVariable String username, Model model, HttpServletResponse response) {
        Optional<User> user = userService.findUserByUserName(username);
        if (user.isPresent()) {
            model.addAttribute("user", user.get());
            return "user/detail";  // Widok "detail.html"
        } else {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            model.addAttribute("error", "User not found");
            return "error/404";  // Widok 404
        }
    }


    // Pobranie użytkownika po adresie e-mail
    @GetMapping("/email/{email}")
    public String getUserByEmail(@PathVariable String email, Model model, HttpServletResponse response) {
        Optional<User> user = userService.findUserByEmail(email);
        if (user.isPresent()) {
            model.addAttribute("user", user.get());
            return "user/detail";  // Widok "detail.html" w katalogu "users"
        } else {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            model.addAttribute("error", "User not found");
            return "error/404";  // Widok strony błędu 404
        }
    }

    // Formularz dodawania nowego użytkownika
    @GetMapping("/new")
    public String showNewUserForm(Model model) {
        model.addAttribute("user", new User());
        return "user/form";  // Widok "form.html" w katalogu "users"
    }

    // Formularz edycji użytkownika
    @GetMapping("/edit/{id}")
    public String showEditUserForm(@PathVariable Long id, Model model, HttpServletResponse response) {
        Optional<User> user = userService.findUserById(id);
        if (user.isPresent()) {
            model.addAttribute("user", user.get());
            return "user/form";  // Widok formularza edycji
        } else {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            model.addAttribute("error", "User not found");
            return "error/404";  // Widok 404
        }
    }

    // Zapisywanie nowego lub edytowanego użytkownika
    @PostMapping
    public String saveUser(@Valid @ModelAttribute User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "user/form";  // Jeśli są błędy, wróć do formularza
        }
        userService.saveUser(user);
        return "redirect:/users";  // Po zapisaniu przekierowanie na listę użytkowników
    }

    // Usuwanie użytkownika po ID
    @DeleteMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);  // Usuwanie użytkownika z bazy danych
        return "redirect:/users";  // Przekierowanie po usunięciu
    }
}
