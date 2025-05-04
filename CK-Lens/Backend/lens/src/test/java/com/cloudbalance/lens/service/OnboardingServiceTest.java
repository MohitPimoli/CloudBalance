package com.cloudbalance.lens.service;

import com.cloudbalance.lens.dto.GlobalMessageDTO;
import com.cloudbalance.lens.dto.onboarding.OnboardingRequest;
import com.cloudbalance.lens.entity.Account;
import com.cloudbalance.lens.exception.ResourceAlreadyExistsException;
import com.cloudbalance.lens.repository.AccountRepository;
import com.cloudbalance.lens.service.onboarding.impl.OnboardingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OnboardingServiceTest {

    @Mock private AccountRepository accountRepository;
    @Mock private OnboardingServiceImpl onboardingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerAwsAccount_shouldRegisterSuccessfully_whenNoDuplicateExists() {
        OnboardingRequest request = new OnboardingRequest();
        request.setArn("arn:aws:iam::123456789012:role/example");
        request.setAccountName("Test Account");
        request.setAccountNumber(123456789012L);
        request.setAccountRegion("us-west-2");

        when(accountRepository.findByArnOrNumber(request.getArn(), request.getAccountNumber()))
                .thenReturn(Collections.emptyList());

        Account savedAccount = Account.builder()
                .id(1L)
                .arn(request.getArn())
                .accountHolderName(request.getAccountName())
                .accountNumber(request.getAccountNumber())
                .accountRegion(request.getAccountRegion())
                .orphan(true)
                .build();

        when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);

        GlobalMessageDTO response = onboardingService.registerAwsAccount(request);

        assertNotNull(response);
        assertEquals("AWS account added successfully", response.getMessage());

        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(accountCaptor.capture());

        Account captured = accountCaptor.getValue();
        assertEquals(request.getArn(), captured.getArn());
        assertEquals(request.getAccountNumber(), captured.getAccountNumber());
        assertEquals(request.getAccountName(), captured.getAccountHolderName());
        assertEquals("us-west-2", captured.getAccountRegion());
        assertTrue(captured.isOrphan());
    }

    @Test
    void registerAwsAccount_shouldThrowException_whenArnAlreadyExists() {

        OnboardingRequest request = new OnboardingRequest();
        request.setArn("arn:aws:iam::123456789012:role/example");
        request.setAccountName("Duplicate ARN Account");
        request.setAccountNumber(123456789012L);
        request.setAccountRegion("us-west-2");

        Account existingAccount = Account.builder()
                .arn(request.getArn())
                .accountNumber(999999999999L)
                .build();

        when(accountRepository.findByArnOrNumber(anyString(), anyLong()))
                .thenReturn(List.of(existingAccount));

        ResourceAlreadyExistsException ex = assertThrows(ResourceAlreadyExistsException.class,
                () -> onboardingService.registerAwsAccount(request));
        assertEquals("Account with ARN arn:aws:iam::123456789012:role/example already exists", ex.getMessage());
    }

    @Test
    void registerAwsAccount_shouldThrowException_whenAccountNumberAlreadyExists() {

        OnboardingRequest request = new OnboardingRequest();
        request.setArn("arn:aws:iam::123456789012:role/new-role");
        request.setAccountName("Duplicate Number Account");
        request.setAccountNumber(123456789012L);
        request.setAccountRegion("us-east-1");

        Account existingAccount = Account.builder()
                .arn("arn:aws:iam::123456789012:role/other-role")
                .accountNumber(request.getAccountNumber())
                .build();

        when(accountRepository.findByArnOrNumber(anyString(), anyLong()))
                .thenReturn(List.of(existingAccount));

        ResourceAlreadyExistsException ex = assertThrows(ResourceAlreadyExistsException.class,
                () -> onboardingService.registerAwsAccount(request));
        assertEquals("Account with number 123456789012 already exists", ex.getMessage());
    }
}
