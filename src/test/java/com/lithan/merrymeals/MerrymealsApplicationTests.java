package com.lithan.merrymeals;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import com.lithan.merrymeals.entity.Category;
import com.lithan.merrymeals.repository.CategoryRepository;
import com.lithan.merrymeals.service.CategoryService;

@SpringBootTest
class MerrymealsApplicationTests {

	@Mock
    private CategoryRepository categoryRepo;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void testCreateCategory() {
        // Arrange
        Category inputCategory = new Category();
        inputCategory.setId(1L);
        inputCategory.setName("Test Category");

        Category savedCategory = new Category();
        savedCategory.setId(1L);
        savedCategory.setName("Test Category");

        when(categoryRepo.save(inputCategory)).thenReturn(savedCategory);

        // Act
        Category createdCategory = categoryService.create(inputCategory);

        // Assert
        assertEquals(1L, createdCategory.getId());
        assertEquals("Test Category", createdCategory.getName());
    }

	@Test
    void testFindAllCategories() {
        // Arrange
        Category category1 = new Category();
        category1.setId(1L);
        category1.setName("Category 1");

        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("Category 2");

        List<Category> categories = Arrays.asList(category1, category2);

        when(categoryRepo.findAll()).thenReturn(categories);

        // Act
        List<Category> result = categoryService.findAll();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Category 1", result.get(0).getName());
        assertEquals("Category 2", result.get(1).getName());
    }

}
