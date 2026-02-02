package com.dev.quikkkk.repository;

import com.dev.quikkkk.entity.Food;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IFoodRepository extends JpaRepository<Food, String> {
    @Query("FROM Food f WHERE f.active = true")
    Page<Food> getFoodsWhereActiveIsTrue(Pageable pageable);
}
