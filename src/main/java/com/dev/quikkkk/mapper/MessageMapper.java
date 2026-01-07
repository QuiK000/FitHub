package com.dev.quikkkk.mapper;

import com.dev.quikkkk.dto.response.MessageResponse;
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
