package com.cloudbalance.lens.dto.onboarding;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OnboardingRequest {
    @NotBlank(message = "ARN is required")
    @Pattern(
            regexp = "^arn:aws:iam::\\d{12}:role\\/[\\w+=,.@-]{1,128}$",
            message = "Invalid ARN format"
    )
    private String arn;

    @NotBlank(message = "Account name is required")
    @Pattern(
            regexp = "^[a-zA-Z0-9 _-]{3,50}$",
            message = "Invalid account name format"
    )
    private String accountName;

    @NotNull(message = "Account number is required")
    @Min(value = 100000000000L, message = "Account number must be 12 digits")
    @Max(value = 999999999999L, message = "Account number must be 12 digits")
    private Long accountNumber;

    @NotBlank(message = "Account region is required")
    @Pattern(
            regexp = "^(us|eu|ap|sa|ca|me|af)-[a-z]+-\\d+$",
            message = "Invalid account region format"
    )
    private String accountRegion;
}
