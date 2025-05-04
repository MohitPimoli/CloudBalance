package com.cloudbalance.lens.controller;

import com.cloudbalance.lens.dto.GlobalMessageDTO;
import com.cloudbalance.lens.dto.onboarding.OnboardingRequest;
import com.cloudbalance.lens.service.onboarding.OnboardingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OnboardingController.class)
class OnboardingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private OnboardingService onboardingService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testRegisterAwsAccount_validRequest_returnsSuccess() throws Exception {
        OnboardingRequest request = OnboardingRequest.builder()
                .arn("arn:aws:iam::123456789012:role/TestRole")
                .accountName("TestAccount")
                .accountNumber(123456789012L)
                .accountRegion("us-east-1")
                .build();

        GlobalMessageDTO response = GlobalMessageDTO.builder()
                .message("Account registered successfully")
                .build();

        Mockito.when(onboardingService.registerAwsAccount(any(OnboardingRequest.class))).thenReturn(response);

        mockMvc.perform(post("/onboarding/aws-account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Account registered successfully"))
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testRegisterAwsAccount_invalidArn_returnsBadRequest() throws Exception {
        OnboardingRequest request = OnboardingRequest.builder()
                .arn("invalid-arn-format")
                .accountName("TestAccount")
                .accountNumber(123456789012L)
                .accountRegion("us-east-1")
                .build();

        mockMvc.perform(post("/onboarding/aws-account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());
    }
}
