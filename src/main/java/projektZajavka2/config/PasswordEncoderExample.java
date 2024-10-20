package projektZajavka2.config;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderExample {
    public static void main(String[] args) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        // Surowe hasło
        String plainPassword = "password";

        // Zaszyfrowanie hasła
        String encodedPassword = passwordEncoder.encode(plainPassword);
        System.out.println("Zaszyfrowane hasło: " + encodedPassword);

        // Weryfikacja, czy surowe hasło pasuje do zakodowanego
        boolean isPasswordMatch = passwordEncoder.matches(plainPassword, encodedPassword);
        System.out.println("Czy hasło pasuje? " + isPasswordMatch);
    }
}

