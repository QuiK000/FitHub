package com.dev.quikkkk.repository;

import com.dev.quikkkk.config.TestCacheConfig;
import com.dev.quikkkk.integration.AbstractIntegrationTest;
import com.dev.quikkkk.modules.nutrition.entity.Food;
import com.dev.quikkkk.modules.nutrition.repository.IFoodRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@Import(TestCacheConfig.class)
@DisplayName("FoodRepository Tests")
class FoodRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private IFoodRepository foodRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("Should find active foods")
    void getFoodsWhereActiveIsTrue_WithActiveFoods_ReturnsPage() {
        String marker = UUID.randomUUID().toString().substring(0, 8);
        persistFood("Chicken " + marker, "Tyson", "111111" + marker, true);
        persistFood("Deleted " + marker, "Brand", "222222" + marker, false);

        Page<Food> result = foodRepository.getFoodsWhereActiveIsTrue(PageRequest.of(0, 10));

        assertThat(result.getContent()).allMatch(Food::isActive);
    }

    @Test
    @DisplayName("Should find food by id when active")
    void findFoodByIdAndActiveIsTrue_WithActiveFood_ReturnsFood() {
        Food food = persistFood("Rice", "Generic", "333333", true);

        Optional<Food> found = foodRepository.findFoodByIdAndActiveIsTrue(food.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Rice");
    }

    @Test
    @DisplayName("Should return empty when food is inactive")
    void findFoodByIdAndActiveIsTrue_WithInactiveFood_ReturnsEmpty() {
        Food food = persistFood("Deleted", "Brand", "444444", false);

        Optional<Food> found = foodRepository.findFoodByIdAndActiveIsTrue(food.getId());

        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should search food by query")
    void findFoodByQuery_WithQuery_ReturnsMatchingFoods() {
        String marker = UUID.randomUUID().toString().substring(0, 8);
        persistFood("Organic Apple " + marker, "Farm", null, true);
        persistFood("Banana " + marker, "Chiquita", null, true);

        Page<Food> result = foodRepository.findFoodByQuery("apple", PageRequest.of(0, 10));

        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getContent().get(0).getName()).containsIgnoringCase("apple");
    }

    @Test
    @DisplayName("Should check barcode existence")
    void existsByBarcodeAndActiveIsTrue_WithExistingBarcode_ReturnsTrue() {
        String barcode = "555555" + UUID.randomUUID().toString().substring(0, 4);
        persistFood("Eggs", "Farm", barcode, true);

        boolean exists = foodRepository.existsByBarcodeAndActiveIsTrue(barcode);

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should check name+brand existence")
    void existsByNameAndBrandAndActiveIsTrue_WithExistingFood_ReturnsTrue() {
        String marker = UUID.randomUUID().toString().substring(0, 8);
        persistFood("Special Milk " + marker, "Dairy Co", null, true);

        boolean exists = foodRepository.existsByNameAndBrandAndActiveIsTrue(
                "Special Milk " + marker, "Dairy Co");

        assertThat(exists).isTrue();
    }

    private Food persistFood(String name, String brand, String barcode, boolean active) {
        Food food = Food.builder()
                .name(name)
                .brand(brand)
                .barcode(barcode)
                .active(active)
                .createdDate(LocalDateTime.now())
                .createdBy("test")
                .build();
        entityManager.persist(food);
        entityManager.flush();
        return food;
    }
}
