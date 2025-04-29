package com.cloudbalance.lens.controller;

import com.cloudbalance.lens.dto.onboarding.OnboardingRequest;
import com.cloudbalance.lens.service.onboarding.OnboardingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/onboarding")
public class OnboardingController {


    private final OnboardingService onboardingService;
    public OnboardingController(OnboardingService onboardingService) {
        this.onboardingService = onboardingService;
    }

    @PostMapping("/aws-account")                     // add AWs account to DB
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> registerAwsAccount(@Valid @RequestBody  OnboardingRequest onboardingRequest) {
        return ResponseEntity.ok(onboardingService.registerAwsAccount(onboardingRequest));
    }

}
