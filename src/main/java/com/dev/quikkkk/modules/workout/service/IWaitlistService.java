package com.dev.quikkkk.modules.workout.service;

import com.dev.quikkkk.modules.workout.dto.response.WaitlistResponse;

public interface IWaitlistService {
    WaitlistResponse joinWaitlist(String sessionId);

    void leaveWaitlist(String sessionId);
}
