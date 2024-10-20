package projektZajavka2.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import projektZajavka2.entity.Category;
import projektZajavka2.repository.CategoryRepository;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CategoryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    public void setUp() {

        // Przygotowanie danych
        Category category = new Category();
        category.setName("Test Category");
        category = categoryRepository.save(category);
        System.out.println(category);
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    public void testGetAllCategories() throws Exception {
        // When & Then
        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(view().name("categories/list"))
                .andExpect(model().attributeExists("categories"))
                .andExpect(model().attribute("categories", hasSize(1)))
                .andExpect(model().attribute("categories", hasItem(
                        allOf(
                                hasProperty("name", is("Test Category"))
                        )
                )));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    public void testShowNewCategoryForm() throws Exception {
        mockMvc.perform(get("/categories/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("categories/form"))
                .andExpect(model().attributeExists("category"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    public void testSaveCategory() throws Exception {
        // Wykonaj żądanie POST do zapisywania kategorii
        mockMvc.perform(post("/categories") // Tworzy żądanie POST do endpointu "/categories"
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED) // Ustawia typ treści na "application/x-www-form-urlencoded"
                        .param("name", "Desserts") // Przekazuje nazwę kategorii jako parametr
                        .with(SecurityMockMvcRequestPostProcessors.csrf())) // Dodaje CSRF token do żądania
                .andExpect(status().is3xxRedirection()) // Sprawdza, czy odpowiedź ma status 3xx (przekierowanie)
                .andExpect(redirectedUrl("/categories")); // Sprawdza, czy przekierowano do "/categories"

        // Zweryfikuj, czy kategoria została zapisana
        Category savedCategory = categoryRepository.findAll().stream() // Pobiera wszystkie kategorie z repozytorium jako strumień
                .filter(c -> c.getName().equals("Desserts")) // Filtrowanie, aby znaleźć kategorię o nazwie "Desserts"
                .findFirst() // Zwraca pierwszy znaleziony element
                .orElse(null); // Jeśli nie znaleziono, zwraca null

        assertNotNull(savedCategory); // Upewnia się, że kategoria nie jest nullem (została zapisana)
        assertEquals("Desserts", savedCategory.getName()); // Sprawdza, czy nazwa zapisanej kategorii to "Desserts"
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    public void testDeleteCategory() throws Exception {
        // Używamy kategorii utworzonej w metodzie setUp()
        List<Category> categories = categoryRepository.findAll();
        assertFalse(categories.isEmpty(), "Brak kategorii do usunięcia");

        // Pobranie ID pierwszej kategorii
        Long categoryId = categories.get(0).getId();

        // Wykonanie żądania DELETE do usunięcia kategorii
        mockMvc.perform(MockMvcRequestBuilders.delete("/categories/delete/{id}", categoryId)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())) // Dodaje token CSRF do żądania
                .andExpect(status().is3xxRedirection()) // Oczekuje statusu 3xx (przekierowanie)
                .andExpect(redirectedUrl("/categories")); // Oczekuje, że zostanie przekierowane na /categories

        // Weryfikacja, czy kategoria została usunięta
        assertEquals(0, categoryRepository.count()); // Sprawdza, czy liczba kategorii w repozytorium wynosi 0
    }
}



