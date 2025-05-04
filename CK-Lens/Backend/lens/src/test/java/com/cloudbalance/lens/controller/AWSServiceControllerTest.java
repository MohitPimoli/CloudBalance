package com.cloudbalance.lens.controller;

import com.cloudbalance.lens.dto.account.AssignAccountResponse;
import com.cloudbalance.lens.dto.awsservices.ASGDTO;
import com.cloudbalance.lens.dto.awsservices.EC2InstanceDTO;
import com.cloudbalance.lens.dto.awsservices.RDSInstanceDTO;
import com.cloudbalance.lens.service.awsservices.AWSService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AWSServiceController.class)
class AWSServiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private AWSService awsService;

    @InjectMocks
    private AWSServiceController awsServiceController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetEc2Instances_withData() throws Exception {
        List<EC2InstanceDTO> ec2List = List.of(
                EC2InstanceDTO.builder()
                        .id("i-123")
                        .name("TestEC2")
                        .region("us-east-1")
                        .status("running")
                        .build()
        );

        when(awsService.fetchEC2Instances(123L)).thenReturn(ec2List);

        mockMvc.perform(get("/aws/ec2")
                        .param("accountNumber", "123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("i-123"));
    }

    @Test
    void testGetEc2Instances_noContent() throws Exception {
        when(awsService.fetchEC2Instances(123L)).thenReturn(List.of());

        mockMvc.perform(get("/aws/ec2")
                        .param("accountNumber", "123"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetRdsInstances_withData() throws Exception {
        List<RDSInstanceDTO> rdsList = List.of(
                RDSInstanceDTO.builder()
                        .id("rds-001")
                        .name("MyRDS")
                        .region("us-west-2")
                        .status("available")
                        .build()
        );

        when(awsService.fetchRDSInstances(123L)).thenReturn(rdsList);

        mockMvc.perform(get("/aws/rds")
                        .param("accountNumber", "123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("rds-001"));
    }

    @Test
    void testGetRdsInstances_noContent() throws Exception {
        when(awsService.fetchRDSInstances(123L)).thenReturn(List.of());

        mockMvc.perform(get("/aws/rds")
                        .param("accountNumber", "123"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetAutoScalingGroups_withData() throws Exception {
        List<ASGDTO> asgList = List.of(
                ASGDTO.builder()
                        .name("ASG-Test")
                        .desiredCapacity(2)
                        .minSize(1)
                        .maxSize(3)
                        .region("ap-south-1")
                        .build()
        );

        when(awsService.fetchAutoScalingGroups(123L)).thenReturn(asgList);

        mockMvc.perform(get("/aws/asg")
                        .param("accountNumber", "123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("ASG-Test"));
    }

    @Test
    void testGetAutoScalingGroups_noContent() throws Exception {
        when(awsService.fetchAutoScalingGroups(123L)).thenReturn(List.of());

        mockMvc.perform(get("/aws/asg")
                        .param("accountNumber", "123"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetLinkedAccounts() throws Exception {
        List<AssignAccountResponse> list = List.of(
                AssignAccountResponse.builder()
                        .accountId(1L)
                        .accountHolderName("TestAccount")
                        .accountNumber(123456789012L)
                        .build()
        );

        when(awsService.fetchLinkedAccounts(1L)).thenReturn(list);

        mockMvc.perform(get("/aws/linked-accounts")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].accountName").value("TestAccount"));
    }

    @Test
    void testGetAllAccounts() throws Exception {
        List<AssignAccountResponse> list = List.of(
                AssignAccountResponse.builder()
                        .accountId(2L)
                        .accountHolderName("ProdAccount")
                        .accountNumber(210987654321L)
                        .build()
        );

        when(awsService.fetchAllAccounts()).thenReturn(list);

        mockMvc.perform(get("/aws/all-accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].accountId").value(2));
    }

    @Test
    void testGetAccountsByRole() throws Exception {
        List<AssignAccountResponse> list = List.of(
                AssignAccountResponse.builder()
                        .accountId(3L)
                        .accountHolderName("CustomerAccount")
                        .accountNumber(999999999999L)
                        .build()
        );

        when(awsService.fetchAccountsByRole()).thenReturn(list);

        mockMvc.perform(get("/aws/account-by-role"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].accountName").value("CustomerAccount"));
    }
}
