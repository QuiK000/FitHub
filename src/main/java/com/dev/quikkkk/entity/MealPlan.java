package com.dev.quikkkk.entity;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "meal_plans")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class MealPlan extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private ClientProfile client;

    @Column(name = "plan_date", nullable = false)
    private LocalDate planDate;

    @Column(name = "total_calories")
    private Integer totalCalories;

    @Column(name = "target_calories")
    private Integer targetCalories;

    @Embedded
    private MacroNutrients macros;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "protein", column = @Column(name = "target_protein")),
            @AttributeOverride(name = "carbs", column = @Column(name = "target_carbs")),
            @AttributeOverride(name = "fats", column = @Column(name = "target_fats")),
            @AttributeOverride(name = "fiber", column = @Column(name = "target_fiber")),
            @AttributeOverride(name = "sugar", column = @Column(name = "target_sugar"))
    })
    private MacroNutrients targetMacros;

    @OneToMany(mappedBy = "mealPlan", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Meal> meals = new HashSet<>();

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}
