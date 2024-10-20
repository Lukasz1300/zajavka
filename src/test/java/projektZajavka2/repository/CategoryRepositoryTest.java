package projektZajavka2.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import projektZajavka2.entity.Category;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    // Variables to hold IDs of categories
    private Long electronicsId;
    private Long booksId;
    private Long clothingId;

    @BeforeEach
    public void setUp() {
        // Save initial categories and capture IDs
        electronicsId = categoryRepository.save(new Category(null, "Electronics", null)).getId();
        booksId = categoryRepository.save(new Category(null, "Books", null)).getId();
        clothingId = categoryRepository.save(new Category(null, "Clothing", null)).getId();
    }

    @Test
    public void whenFindById_thenReturnCategory() {
        assertThat(categoryRepository.findById(electronicsId))
                .isPresent()
                .hasValueSatisfying(category -> assertThat(category.getName()).isEqualTo("Electronics"));
    }

    @Test
    public void whenFindAll_thenReturnCategories() {
        List<Category> categories = categoryRepository.findAll();

        assertThat(categories)
                .isNotEmpty()
                .hasSize(3)
                .extracting(Category::getName)
                .containsExactlyInAnyOrder("Electronics", "Books", "Clothing");
    }

    @Test
    public void whenSaveCategory_thenCategoryShouldBeSaved() {
        Category savedCategory = categoryRepository.save(new Category(null, "Home & Garden", null));

        assertThat(savedCategory)
                .isNotNull()
                .extracting(Category::getName)
                .isEqualTo("Home & Garden");

        assertThat(categoryRepository.findById(savedCategory.getId()))
                .isPresent()
                .hasValueSatisfying(category -> assertThat(category.getName()).isEqualTo("Home & Garden"));
    }

    @Test
    public void whenDeleteCategory_thenCategoryShouldBeDeleted() {
        Category categoryToDelete = new Category(null, "Toys", null);
        Long id = categoryRepository.save(categoryToDelete).getId();

        categoryRepository.deleteById(id);

        assertThat(categoryRepository.findById(id)).isNotPresent();
    }
}
