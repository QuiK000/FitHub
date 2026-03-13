package com.dev.quikkkk.modules.notification.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class NotificationSummaryResponse {
    private Long unreadCount;
    private List<NotificationResponse> recentNotifications;
}
