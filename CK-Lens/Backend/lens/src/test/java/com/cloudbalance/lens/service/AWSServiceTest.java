package com.cloudbalance.lens.service;

import com.cloudbalance.lens.config.AwsConfig;
import com.cloudbalance.lens.dto.account.AssignAccountResponse;
import com.cloudbalance.lens.dto.awsservices.ASGDTO;
import com.cloudbalance.lens.dto.awsservices.EC2InstanceDTO;
import com.cloudbalance.lens.dto.awsservices.RDSInstanceDTO;
import com.cloudbalance.lens.entity.Account;
import com.cloudbalance.lens.entity.User;
import com.cloudbalance.lens.exception.CustomException;
import com.cloudbalance.lens.exception.ResourceNotFoundException;
import com.cloudbalance.lens.repository.AccountRepository;
import com.cloudbalance.lens.repository.UserRepository;
import com.cloudbalance.lens.service.awsservices.impl.AWSServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import software.amazon.awssdk.services.autoscaling.AutoScalingClient;
import software.amazon.awssdk.services.autoscaling.model.AutoScalingGroup;
import software.amazon.awssdk.services.autoscaling.model.DescribeAutoScalingGroupsResponse;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;
import software.amazon.awssdk.services.rds.RdsClient;
import software.amazon.awssdk.services.rds.model.DBInstance;
import software.amazon.awssdk.services.rds.model.DescribeDbInstancesResponse;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AWSServiceTest {

    @Mock private AwsConfig awsConfig;
    @Mock private UserRepository userRepository;
    @Mock private AccountRepository accountRepository;
    @InjectMocks private AWSServiceImpl awsService;

    private Account account;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        account = new Account();
        account.setAccountNumber(12345L);
        account.setArn("arn:aws:iam::123456789012:role/MyRole");
        account.setAccountRegion("us-west-2");
        user = new User();
        user.setId(1L);
        user.setUsername("testUser");
    }

    @Test
    void testFetchEC2Instances_Success() {
        when(accountRepository.findByAccountNumber(12345L)).thenReturn(Optional.of(account));

        Ec2Client ec2Client = mock(Ec2Client.class);
        when(awsConfig.ec2Client(any(), eq("us-west-2"))).thenReturn(ec2Client);

        Reservation reservation = mock(Reservation.class);
        Instance instance = mock(Instance.class);
        when(instance.instanceId()).thenReturn("i-123456");
        when(instance.state().nameAsString()).thenReturn("running");
        when(instance.placement().availabilityZone()).thenReturn("us-west-2a");
        when(instance.tags()).thenReturn(Collections.singletonList(Tag.builder().key("Name").value("TestInstance").build()));
        when(reservation.instances()).thenReturn(Collections.singletonList(instance));

        when(ec2Client.describeInstances((DescribeInstancesRequest) any())).thenReturn(mock(DescribeInstancesResponse.class));
        List<EC2InstanceDTO> instances = awsService.fetchEC2Instances(12345L);

        assertNotNull(instances);
        assertEquals(1, instances.size());
        assertEquals("TestInstance", instances.getFirst().getName());
        assertEquals("i-123456", instances.getFirst().getId());
    }

    @Test
    void testFetchEC2Instances_AccountNotFound() {
        when(accountRepository.findByAccountNumber(12345L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> awsService.fetchEC2Instances(12345L));
    }

    @Test
    void testFetchRDSInstances_Success() {

        when(accountRepository.findByAccountNumber(12345L)).thenReturn(Optional.of(account));

        RdsClient rdsClient = mock(RdsClient.class);
        when(awsConfig.rdsClient(any(), eq("us-west-2"))).thenReturn(rdsClient);

        DBInstance dbInstance = mock(DBInstance.class);
        when(dbInstance.dbiResourceId()).thenReturn("db-123456");
        when(dbInstance.dbInstanceIdentifier()).thenReturn("TestDBInstance");
        when(dbInstance.dbInstanceStatus()).thenReturn("available");
        when(dbInstance.availabilityZone()).thenReturn("us-west-2a");
        when(dbInstance.engine()).thenReturn("MySQL");

        when(rdsClient.describeDBInstances()).thenReturn(mock(DescribeDbInstancesResponse.class));
        List<RDSInstanceDTO> rdsInstances = awsService.fetchRDSInstances(12345L);

        assertNotNull(rdsInstances);
        assertEquals(1, rdsInstances.size());
        assertEquals("TestDBInstance", rdsInstances.getFirst().getName());
        assertEquals("available", rdsInstances.getFirst().getStatus());
    }

    @Test
    void testFetchRDSInstances_AccountNotFound() {
        when(accountRepository.findByAccountNumber(12345L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> awsService.fetchRDSInstances(12345L));
    }

    @Test
    void testFetchAutoScalingGroups_Success() {

        when(accountRepository.findByAccountNumber(12345L)).thenReturn(Optional.of(account));
        AutoScalingClient asgClient = mock(AutoScalingClient.class);
        when(awsConfig.autoScalingClient(any(), eq("us-west-2"))).thenReturn(asgClient);
        AutoScalingGroup asg = mock(AutoScalingGroup.class);
        when(asg.autoScalingGroupName()).thenReturn("TestASG");
        when(asg.availabilityZones()).thenReturn(List.of("us-west-2a"));
        when(asg.status()).thenReturn("active");

        when(asgClient.describeAutoScalingGroups()).thenReturn(mock(DescribeAutoScalingGroupsResponse.class));
        List<ASGDTO> asgs = awsService.fetchAutoScalingGroups(12345L);

        assertNotNull(asgs);
        assertEquals(1, asgs.size());
        assertEquals("TestASG", asgs.getFirst().getName());
        assertEquals("active", asgs.getFirst().getStatus());
    }

    @Test
    void testFetchAutoScalingGroups_AccountNotFound() {
        when(accountRepository.findByAccountNumber(12345L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> awsService.fetchAutoScalingGroups(12345L));
    }

    @Test
    void testFetchAccountsByRole_CustomerRole() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testUser");
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_CUSTOMER"));
        doReturn(authorities).when(authentication).getAuthorities();
        SecurityContextHolder.setContext(new SecurityContextImpl(authentication));
        List<AssignAccountResponse> accounts = awsService.fetchAccountsByRole();
        assertNotNull(accounts);
        assertTrue(accounts.isEmpty());
    }


    @Test
    void testFetchLinkedAccounts_UserNotFound() {

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CustomException.UserNotFoundException.class, () -> awsService.fetchLinkedAccounts(1L));
    }

    @Test
    void testFetchAllAccounts_Success() {

        List<Account> allAccounts = Arrays.asList(account, new Account());
        when(accountRepository.findAll()).thenReturn(allAccounts);

        List<AssignAccountResponse> accounts = awsService.fetchAllAccounts();
        assertNotNull(accounts);
        assertEquals(2, accounts.size());
    }
}
