package com.dev.quikkkk.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class AttendanceStatsResponse {
    private LocalDate date;
    private Long checkIns;

    public AttendanceStatsResponse(Date date, Long checkIns) {
        this.date = date.toLocalDate();
        this.checkIns = checkIns;
    }
}

