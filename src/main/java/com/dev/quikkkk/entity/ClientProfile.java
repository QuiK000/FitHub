package com.dev.quikkkk.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Table(name = "clients_profiles")
@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClientProfile extends BaseEntity {
    @Column(name = "first_name")
    private String firstname;

    @Column(name = "last_name")
    private String lastname;

    @Column(name = "phone", unique = true)
    private String phone;

    @Column(name = "birth_date")
    private LocalDateTime birthdate;

    @Column(name = "height", nullable = false)
    private double height;

    @Column(name = "weight", nullable = false)
    private double weight;

    @Column(name = "active", nullable = false)
    private boolean active;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    public void clearPersonalData() {
        this.firstname = null;
        this.lastname = null;
        this.phone = null;
        this.birthdate = null;
    }
}
