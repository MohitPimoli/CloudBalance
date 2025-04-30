package com.cloudbalance.lens.service.onboarding.impl;

import com.cloudbalance.lens.dto.GlobalMessageDTO;
import com.cloudbalance.lens.dto.onboarding.OnboardingRequest;
import com.cloudbalance.lens.entity.Account;
import com.cloudbalance.lens.exception.ResourceAlreadyExistsException;
import com.cloudbalance.lens.repository.AccountRepository;
import com.cloudbalance.lens.service.onboarding.OnboardingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class OnboardingServiceImpl implements OnboardingService {

    private final AccountRepository accountRepository;
    public OnboardingServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public GlobalMessageDTO registerAwsAccount(OnboardingRequest onboardingRequest) {
        log.info("Registering new AWS account: {}", onboardingRequest);

        String arn = onboardingRequest.getArn();
        String name = onboardingRequest.getAccountName();
        Long number = onboardingRequest.getAccountNumber();
        String region = onboardingRequest.getAccountRegion();

        List<Account> existingAccounts = accountRepository.findByArnOrNumber(arn, number);
        for (Account account : existingAccounts) {
            if (account.getArn().equals(arn)) {
                log.warn("Attempt to register duplicate ARN: {}", arn);
                throw new ResourceAlreadyExistsException("Account with ARN " + arn + " already exists");
            }
            if (account.getAccountNumber().equals(number)) {
                log.warn("Attempt to register duplicate account number: {}", number);
                throw new ResourceAlreadyExistsException("Account with number " + number + " already exists");
            }
        }
        Account account = Account.builder()
                .arn(arn.trim())
                .accountNumber(number)
                .accountHolderName(name.trim())
                .accountRegion(region)
                .orphan(true)
                .build();

        Account savedAccount = accountRepository.save(account);
        log.info("Successfully onboarded AWS account: ID={}, Name={}", savedAccount.getId(),
                savedAccount.getAccountHolderName());
        return GlobalMessageDTO.builder()
                .message("AWS account added successfully")
                .build();
    }
}
