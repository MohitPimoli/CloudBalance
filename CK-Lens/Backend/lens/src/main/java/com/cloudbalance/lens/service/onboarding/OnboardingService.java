package com.cloudbalance.lens.service.onboarding;

import com.cloudbalance.lens.dto.GlobalMessageDTO;
import com.cloudbalance.lens.dto.onboarding.OnboardingRequest;

public interface OnboardingService {
    GlobalMessageDTO registerAwsAccount(OnboardingRequest onboardingRequest);

}
