package com.dev.quikkkk.modules.workout.mapper;

import com.dev.quikkkk.modules.user.dto.response.ClientShortResponse;
import com.dev.quikkkk.modules.user.entity.ClientProfile;
import com.dev.quikkkk.modules.workout.dto.response.WaitlistResponse;
import com.dev.quikkkk.modules.workout.entity.SessionWaitlist;
import com.dev.quikkkk.modules.workout.entity.TrainingSession;
import com.dev.quikkkk.modules.workout.enums.WaitlistStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class WaitlistMapper {
    public SessionWaitlist toEntity(TrainingSession session, ClientProfile client, int newPosition) {
        return SessionWaitlist.builder()
                .session(session)
                .client(client)
                .position(newPosition)
                .status(WaitlistStatus.WAITING)
                .joinedAt(LocalDateTime.now())
                .build();
    }

    public WaitlistResponse toResponse(SessionWaitlist waitlist) {
        return WaitlistResponse.builder()
                .id(waitlist.getId())
                .position(waitlist.getPosition())
                .status(waitlist.getStatus())
                .joinedAt(waitlist.getJoinedAt())
                .message("You are successfully joined at session. Your position: " + waitlist.getPosition()) // TODO
                .estimatedWait("TODO") // TODO
                .client(ClientShortResponse.builder()
                        .clientId(waitlist.getClient().getId())
                        .clientFirstname(waitlist.getClient().getFirstname())
                        .clientLastname(waitlist.getClient().getLastname())
                        .build())
                .build();
    }
}
