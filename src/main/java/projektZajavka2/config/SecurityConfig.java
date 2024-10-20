package projektZajavka2.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import projektZajavka2.service.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/h2-console/**", "/security/login")
                )
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                // Admin ma dostęp do wszystkich metod HTTP i ścieżek
                                .requestMatchers("/**").hasRole("ADMIN")
                                // Dla użytkownika dostęp do niektórych zasobów (można dodać więcej)
                                .requestMatchers("/api/categories").hasAnyRole("ADMIN", "USER")
                                .requestMatchers("/api/menuItems").permitAll()
                                // Publiczny dostęp do stron logowania i konsoli H2
                                .requestMatchers("/security/login", "/h2-console/**").permitAll()
                                // Pozostałe zapytania wymagają autoryzacji
                                .anyRequest().authenticated()
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/security/login")
                        .defaultSuccessUrl("/home", true)
                        .permitAll()
                )
                .logout(LogoutConfigurer::permitAll);

        return http.build();
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder()); // Dodanie kodera haseł
        return authenticationManagerBuilder.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Bean kodera haseł
    }
}
