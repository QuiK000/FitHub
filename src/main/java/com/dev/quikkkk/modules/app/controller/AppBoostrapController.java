package com.dev.quikkkk.modules.app.controller;

import com.dev.quikkkk.modules.app.dto.response.AppBootstrapResponse;
import com.dev.quikkkk.modules.app.services.IAppBootstrapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/app")
@RequiredArgsConstructor
public class AppBoostrapController {
    private final IAppBootstrapService appBootstrapService;

    @GetMapping("/bootstrap")
    public ResponseEntity<AppBootstrapResponse> getBootstrap() {
        return ResponseEntity.ok(appBootstrapService.getBootstrap());
    }
}
