package com.dev.quikkkk.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class RevenueStatsResponse {
    private LocalDate date;
    private BigDecimal revenue;

    public RevenueStatsResponse(Date date, BigDecimal revenue) {
        this.date = date.toLocalDate();
        this.revenue = revenue;
    }
}
