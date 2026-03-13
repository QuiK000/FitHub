package com.dev.quikkkk.core.mapper;

import com.dev.quikkkk.core.dto.MessageResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class MessageMapper {
    public MessageResponse message(String message) {
        return MessageResponse.builder()
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
