package com.dev.quikkkk.core.utils;

import com.dev.quikkkk.core.enums.ErrorCode;
import com.dev.quikkkk.core.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class TimeAgoFormatter {
    private final Clock clock;

    public String format(LocalDateTime createdDate) {
        if (createdDate == null) throw new BusinessException(ErrorCode.CREATED_DATE_IS_NULL);

        LocalDateTime now = LocalDateTime.now(clock);
        Duration duration = Duration.between(createdDate, now);

        long seconds = duration.getSeconds();
        long minutes = duration.toMinutes();
        long hours = duration.toHours();
        long days = duration.toDays();

        if (seconds < 60) return "Just now";
        if (minutes < 60) return minutes + (minutes == 1 ? " min ago" : " mins ago");
        if (hours < 24) return hours + (hours == 1 ? " hour ago" : " hours ago");
        if (days < 7) return days + (days == 1 ? " day ago" : " days ago");

        if (days < 30) {
            long weeks = days / 7;
            return weeks + (weeks == 1 ? " week ago" : " weeks ago");
        }

        if (days < 365) {
            long months = days / 30;
            return months + (months == 1 ? " month ago" : " months ago");
        }

        long years = days / 365;
        return years + (years == 1 ? " year ago" : " years ago");
    }
}
