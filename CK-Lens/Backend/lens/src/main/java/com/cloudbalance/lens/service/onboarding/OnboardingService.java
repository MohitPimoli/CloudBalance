package com.cloudbalance.lens.service.onboarding;

import com.cloudbalance.lens.dto.onboarding.OnboardingRequest;

public interface OnboardingService {
    String registerAwsAccount(OnboardingRequest onboardingRequest);

}
