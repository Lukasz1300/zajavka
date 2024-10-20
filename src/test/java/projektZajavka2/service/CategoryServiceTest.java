package projektZajavka2.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import projektZajavka2.entity.Category;
import projektZajavka2.repository.CategoryRepository;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    public void testFindAllCategories() {

        Category category1 = new Category();
        category1.setId(1L);
        category1.setName("Category1");

        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("Category2");

        when(categoryRepository.findAll()).thenReturn(Arrays.asList(category1, category2));

        List<Category> categories = categoryService.findAllCategories();

        assertEquals(2, categories.size());
        assertEquals("Category1", categories.get(0).getName());
        assertEquals("Category2", categories.get(1).getName());
    }

    @Test
    public void testSaveCategory() {
        Category category = new Category();
        category.setName("New Category");

        when(categoryRepository.save(category)).thenReturn(category);

        Category savedCategory = categoryService.saveCategory(category);

        assertEquals("New Category", savedCategory.getName());
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    public void testDeleteCategoryById() {
        Long categoryId = 1L;

        categoryService.deleteCategoryById(categoryId);

        verify(categoryRepository, times(1)).deleteById(categoryId);

    }
}
