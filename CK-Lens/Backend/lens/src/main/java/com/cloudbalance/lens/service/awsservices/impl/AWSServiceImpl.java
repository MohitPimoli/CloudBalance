package com.cloudbalance.lens.service.awsservices.impl;

import com.cloudbalance.lens.config.AwsConfig;
import com.cloudbalance.lens.dto.account.AssignAccountResponse;
import com.cloudbalance.lens.dto.awsservices.ASGDTO;
import com.cloudbalance.lens.dto.awsservices.EC2InstanceDTO;
import com.cloudbalance.lens.dto.awsservices.RDSInstanceDTO;
import com.cloudbalance.lens.entity.Account;
import com.cloudbalance.lens.entity.User;
import com.cloudbalance.lens.exception.ApiException;
import com.cloudbalance.lens.exception.CustomException;
import com.cloudbalance.lens.exception.GenericApplicationException;
import com.cloudbalance.lens.exception.ResourceNotFoundException;
import com.cloudbalance.lens.repository.AccountRepository;
import com.cloudbalance.lens.repository.UserRepository;
import com.cloudbalance.lens.service.awsservices.AWSService;
import com.cloudbalance.lens.utils.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.services.autoscaling.AutoScalingClient;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesRequest;
import software.amazon.awssdk.services.ec2.model.Tag;
import software.amazon.awssdk.services.rds.RdsClient;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AWSServiceImpl implements AWSService {


    private final AwsConfig awsConfig;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    public AWSServiceImpl(AwsConfig awsConfig, UserRepository userRepository, AccountRepository accountRepository) {
        this.awsConfig = awsConfig;
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public List<EC2InstanceDTO> fetchEC2Instances(Long accountNumber) {
        log.info("Fetching EC2 instances for account number: {}", accountNumber);
        Account account = accountRepository.findByAccountNumber(accountNumber).orElseThrow(() ->
                new ResourceNotFoundException(Constant.ACCOUNT_NOT_FOUND + accountNumber));

        String roleArn = account.getArn();
        String region = account.getAccountRegion();

        AwsCredentialsProvider credentialsProvider = awsConfig.assumeRoleCredentials(awsConfig.stsClient(), roleArn);

        try (Ec2Client ec2Client = awsConfig.ec2Client(credentialsProvider, region)) {
            var response = ec2Client.describeInstances(DescribeInstancesRequest.builder().build());

            var instances = response.reservations().stream()
                    .flatMap(reservation -> reservation.instances().stream())
                    .map(instance -> EC2InstanceDTO.builder()
                            .id(instance.instanceId())
                            .name(instance.tags().stream()
                                    .filter(tag -> "Name".equals(tag.key()))
                                    .map(Tag::value)
                                    .findFirst()
                                    .orElse("N/A"))
                            .region(instance.placement().availabilityZone())
                            .status(instance.state().nameAsString())
                            .build())
                    .collect(Collectors.toList());

            log.info("Fetched {} EC2 instances", instances.size());
            return instances;
        } catch (Exception e) {
            log.error("Error fetching EC2 instances", e);
            throw new ApiException("Failed to fetch EC2 instances");
        }
    }

    @Override
    public List<RDSInstanceDTO> fetchRDSInstances(Long accountNumber) {
        log.info("Fetching RDS instances for account number: {}", accountNumber);
        Account account = accountRepository.findByAccountNumber(accountNumber).orElseThrow(() ->
                new ResourceNotFoundException(Constant.ACCOUNT_NOT_FOUND + accountNumber));

        String roleArn = account.getArn();
        String region = account.getAccountRegion();

        AwsCredentialsProvider credentialsProvider = awsConfig.assumeRoleCredentials(awsConfig.stsClient(), roleArn);

        try (RdsClient rdsClient = awsConfig.rdsClient(credentialsProvider, region)) {
            var response = rdsClient.describeDBInstances();

            var rdsInstances = response.dbInstances().stream()
                    .map(db -> RDSInstanceDTO.builder()
                            .id(db.dbiResourceId())
                            .name(db.dbInstanceIdentifier())
                            .region(db.availabilityZone())
                            .status(db.dbInstanceStatus())
                            .engine(db.engine())
                            .build())
                    .collect(Collectors.toList());

            log.info("Fetched {} RDS instances", rdsInstances.size());
            return rdsInstances;
        } catch (Exception e) {
            log.error("Error fetching RDS instances", e);
            throw new ApiException("Failed to fetch RDS instances");
        }
    }

    @Override
    public List<ASGDTO> fetchAutoScalingGroups(Long accountNumber) {
        log.info("Fetching ASG instances for account number: {}", accountNumber);
        Account account = accountRepository.findByAccountNumber(accountNumber).orElseThrow(() ->
                new ResourceNotFoundException(Constant.ACCOUNT_NOT_FOUND + accountNumber));

        String roleArn = account.getArn();
        String region = account.getAccountRegion();
        AwsCredentialsProvider credentialsProvider = awsConfig.assumeRoleCredentials(awsConfig.stsClient(), roleArn);

        try (AutoScalingClient asgClient = awsConfig.autoScalingClient(credentialsProvider, region)) {
            var response = asgClient.describeAutoScalingGroups();

            var asgs = response.autoScalingGroups().stream()
                    .map(asg -> ASGDTO.builder()
                            .id(asg.autoScalingGroupName())
                            .name(asg.autoScalingGroupName())
                            .region(asg.availabilityZones().isEmpty() ? "N/A" : asg.availabilityZones().get(0))
                            .desiredCapacity(asg.desiredCapacity())
                            .minSize(asg.minSize())
                            .maxSize(asg.maxSize())
                            .status(asg.status() == null ? "N/A" : asg.status())
                            .build())
                    .collect(Collectors.toList());

            log.info("Fetched {} Auto Scaling Groups", asgs.size());
            return asgs;
        } catch (Exception e) {
            log.error("Error fetching Auto Scaling Groups", e);
            throw new ApiException("Failed to fetch Auto Scaling Groups");
        }
    }

    @Override
    public List<AssignAccountResponse> fetchAccountsByRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        boolean isCustomer = roles.contains("ROLE_CUSTOMER");

        if (isCustomer) {
            User user = userRepository.findByUsername(username).orElseThrow(() -> {
                log.warn("User not found with username: {}", username);
                return new CustomException.UserNotFoundException(Constant.USER_NOT_FOUND_WITH_USERNAME + username);
            });
            return fetchLinkedAccounts(user.getId());

        } else {
            return fetchAllAccounts();
        }
    }


    @Override
    public List<AssignAccountResponse> fetchLinkedAccounts(Long userId) {
        log.info("Fetching linked accounts for userId: {}", userId);

        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> {
                        log.warn("User not found with ID: {}", userId);
                        return new CustomException.UserNotFoundException(Constant.USER_NOT_FOUND_WITH_ID + userId);
                    });

            List<AssignAccountResponse> accountDtos = user.getAssignedAccounts().stream()
                    .map(userCloudAccount -> {
                        Account account = userCloudAccount.getCloudAccount();
                        if (account == null) {
                            log.warn("Null CloudAccount encountered for User ID: {}", userId);
                            return null;
                        }
                        return AssignAccountResponse.builder()
                                .accountHolderName(account.getAccountHolderName())
                                .accountNumber(account.getAccountNumber())
                                .build();
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            log.info("Found {} linked accounts for userId: {}", accountDtos.size(), userId);
            return accountDtos;

        } catch (CustomException.UserNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while fetching linked accounts for userId: {}", userId, e);
            throw new GenericApplicationException("Failed to fetch linked accounts", e.getCause());
        }
    }

    @Override
    public List<AssignAccountResponse> fetchAllAccounts() {
        log.info("Fetching all accounts");
        List<Account> allAccounts = accountRepository.findAll();
        List<AssignAccountResponse> accountList = allAccounts.stream()
                .map(account -> AssignAccountResponse.builder()
                        .accountHolderName(account.getAccountHolderName())
                        .accountNumber(account.getAccountNumber())
                        .build())
                .toList();
        log.info("{} accounts are fetched", accountList.size());
        return accountList;
    }

}
