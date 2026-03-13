package com.dev.quikkkk.modules.notification.dto.request;

import com.dev.quikkkk.modules.notification.enums.NotificationPriority;
import com.dev.quikkkk.modules.notification.enums.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BroadcastNotificationRequest {
    @NotNull(message = "VALIDATION.NOTIFICATION.TYPE.NOT_NULL")
    private NotificationType type;

    @NotNull(message = "VALIDATION.NOTIFICATION.PRIORITY.NOT_NULL")
    private NotificationPriority priority;

    @NotBlank(message = "VALIDATION.NOTIFICATION.TITLE.NOT_BLANK")
    @Size(max = 200, message = "VALIDATION.NOTIFICATION.TITLE.SIZE")
    private String title;

    @NotBlank(message = "VALIDATION.NOTIFICATION.MESSAGE.NOT_BLANK")
    private String message;
    private String actionUrl;
    private String referenceId;
    private String referenceType;
    private LocalDateTime scheduledFor;
}
