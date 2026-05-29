package com.dev.quikkkk.modules.notification.dto.telegram;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TelegramMessage {
    @JsonProperty("message_id")
    private Long messageId;

    private String text;
    private TelegramChat chat;
}
