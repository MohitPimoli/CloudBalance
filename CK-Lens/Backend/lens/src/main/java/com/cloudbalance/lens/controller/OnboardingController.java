package com.cloudbalance.lens.controller;

import com.cloudbalance.lens.dto.onboarding.OnboardingRequest;
import com.cloudbalance.lens.service.onboarding.OnboardingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/onboarding")
public class OnboardingController {

    @Autowired
    private OnboardingService onboardingService;

    @PostMapping("/aws-account")                     // add AWs account to DB
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> registerAwsAccount(@Valid @RequestBody  OnboardingRequest onboardingRequest) {
        System.out.println("\nAccount details"+onboardingRequest);
        return ResponseEntity.ok(onboardingService.registerAwsAccount(onboardingRequest));
    }

}
