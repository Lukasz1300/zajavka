package projektZajavka2.controller.rest;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import projektZajavka2.entity.Category;
import projektZajavka2.service.CategoryService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/categories")
// @AllArgsConstructor
public class CategoryRestController {
    private final CategoryService categoryService;

    @Autowired
    public CategoryRestController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // Pobranie wszystkich kategorii
    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.findAllCategories();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    // Dodanie nowej kategorii
    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        Category savedCategory = categoryService.saveCategory(category);
        return new ResponseEntity<>(savedCategory, HttpStatus.CREATED);
    }

    // Aktualizacja kategorii
    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        Optional<Category> existingCategory = categoryService.findCategoryById(id);
        if (existingCategory.isPresent()) {
            category.setId(id);
            Category updatedCategory = categoryService.saveCategory(category);
            return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
        }
        return ResponseEntity.notFound().build();
    }

    // Usuwanie kategorii po ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        Optional<Category> existingCategory = categoryService.findCategoryById(id);
        if (existingCategory.isPresent()) {
            categoryService.deleteCategoryById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
