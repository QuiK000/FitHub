package com.dev.quikkkk.entity;

import com.dev.quikkkk.enums.ServingUnit;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "foods")
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Food extends BaseEntity {
    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "brand", length = 200)
    private String brand;

    @Column(name = "serving_size")
    private Double servingSize;

    @Enumerated(EnumType.STRING)
    @Column(name = "serving_unit")
    private ServingUnit servingUnit;

    @Column(name = "calories_per_serving")
    private Integer caloriesPerServing;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "protein", column = @Column(name = "protein_per_serving")),
            @AttributeOverride(name = "carbs", column = @Column(name = "carbs_per_serving")),
            @AttributeOverride(name = "fats", column = @Column(name = "fats_per_serving")),
            @AttributeOverride(name = "fiber", column = @Column(name = "fiber_per_serving")),
            @AttributeOverride(name = "sugar", column = @Column(name = "sugar_per_serving")),
    })
    private MacroNutrients macrosPerServing;

    @Column(name = "barcode", length = 50)
    private String barcode;

    @Column(name = "active", nullable = false)
    private boolean active;
}
