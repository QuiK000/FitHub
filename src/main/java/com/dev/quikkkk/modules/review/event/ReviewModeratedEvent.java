package com.dev.quikkkk.modules.review.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ReviewModeratedEvent extends ApplicationEvent {
    private final String clientId;
    private final String message;

    public ReviewModeratedEvent(Object source, String clientId, String message) {
        super(source);
        this.clientId = clientId;
        this.message = message;
    }
}
