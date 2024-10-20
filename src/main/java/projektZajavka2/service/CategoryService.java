package projektZajavka2.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import projektZajavka2.entity.Category;
import projektZajavka2.repository.CategoryRepository;

import java.util.List;
import java.util.Optional;

@Transactional
@Service
@Slf4j
@AllArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> findAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        log.info("Retrieved categories: {}", categories);
        return categories;

    }

    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }

    public void deleteCategoryById(Long id) {
        categoryRepository.deleteById(id);
    }

    public Optional<Category> findCategoryById(Long id) {
        return categoryRepository.findById(id);
    }
}


