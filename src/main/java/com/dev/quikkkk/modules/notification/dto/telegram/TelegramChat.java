package com.dev.quikkkk.modules.notification.dto.telegram;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TelegramChat {
    private Long id;
    private String type;
}
