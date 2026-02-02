package com.dev.quikkkk.repository;

import com.dev.quikkkk.entity.Food;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IFoodRepository extends JpaRepository<Food, String> {
    @Query("FROM Food f WHERE f.active = true")
    Page<Food> getFoodsWhereActiveIsTrue(Pageable pageable);

    Optional<Food> findFoodByIdAndActiveIsTrue(String id);

    @Query("""
            SELECT f FROM Food f
            WHERE f.active = true
            AND (:query iS NULL OR TRIM(:query) = ''
                        OR LOWER(f.name) LIKE LOWER(CONCAT('%', :query, '%'))
                        OR LOWER(f.brand) LIKE LOWER(CONCAT('%', :query, '%'))
                ) ORDER BY f.createdDate DESC
            """)
    Page<Food> findFoodByQuery(@Param("query") String query, Pageable pageable);

    Optional<Food> findFoodByBarcodeAndActiveIsTrue(String barcode);

    boolean existsByBarcodeAndActiveIsTrue(String barcode);

    boolean existsByNameAndBrandAndActiveIsTrue(String name, String brand);
}
