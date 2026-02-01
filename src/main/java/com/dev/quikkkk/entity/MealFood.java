package com.dev.quikkkk.entity;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "meal_foods")
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class MealFood extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meal_id", nullable = false)
    private Meal meal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_id", nullable = false)
    private Food food;

    @Column(name = "servings", nullable = false)
    private Double servings;

    @Column(name = "total_calories")
    private Integer totalCalories;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "protein", column = @Column(name = "total_protein")),
            @AttributeOverride(name = "carbs", column = @Column(name = "total_carbs")),
            @AttributeOverride(name = "fats", column = @Column(name = "total_fats")),
            @AttributeOverride(name = "fiber", column = @Column(name = "total_fiber")),
            @AttributeOverride(name = "sugar", column = @Column(name = "total_sugar"))
    })
    private MacroNutrients totalMacros;
}
