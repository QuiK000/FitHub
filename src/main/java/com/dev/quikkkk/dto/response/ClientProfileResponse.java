package com.dev.quikkkk.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClientProfileResponse {
    private String firstname;
    private String lastname;
    private String phone;
    private LocalDateTime birthdate;
    private Double height;
    private Double weight;
    private boolean active;
    private LocalDateTime createdAt;
}
