package projektZajavka2.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import projektZajavka2.entity.Category;
import projektZajavka2.service.CategoryService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
@WithMockUser(username = "testuser", roles = {"ADMIN"})
@ActiveProfiles("test")
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Test

    void testGetAllCategories() throws Exception {
        Category category1 = new Category();
        category1.setId(1L);
        category1.setName("Category1");

        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("Category2");

        List<Category> categories = Arrays.asList(category1, category2);

        when(categoryService.findAllCategories()).thenReturn(categories);

        mockMvc.perform(MockMvcRequestBuilders.get("/categories"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("categories"))
                .andExpect(MockMvcResultMatchers.view().name("categories/list"));
    }

    @Test

    void testShowNewCategoryForm() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/categories/new"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("categories/form"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("category"));
    }

    @Test

    void testSaveCategory() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/categories")
                        .param("name", "New Category")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/categories"));

        Category category = new Category();
        category.setName("New Category");
        verify(categoryService).saveCategory(category);
    }

    @Test
    void testDeleteCategory() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/categories/delete/1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/categories"));
        verify(categoryService, times(1)).deleteCategoryById(1L);
    }
}
