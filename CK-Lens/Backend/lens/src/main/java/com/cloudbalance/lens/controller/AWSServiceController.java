package com.cloudbalance.lens.controller;

import com.cloudbalance.lens.dto.account.AssignAccountResponse;
import com.cloudbalance.lens.dto.awsservices.ASGDTO;
import com.cloudbalance.lens.dto.awsservices.EC2InstanceDTO;
import com.cloudbalance.lens.dto.awsservices.RDSInstanceDTO;
import com.cloudbalance.lens.service.awsservices.AWSService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/aws")
public class AWSServiceController {


    private final AWSService awsService;

    public AWSServiceController(AWSService awsService) {
        this.awsService = awsService;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'READ-ONLY','CUSTOMER')")
    @GetMapping("/ec2")
    public ResponseEntity<List<EC2InstanceDTO>> getEc2Instances(@RequestParam("accountNumber") Long accountNumber) {
        List<EC2InstanceDTO> ec2Instances = awsService.fetchEC2Instances(accountNumber);
        if (ec2Instances.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(ec2Instances);
    }
    @PreAuthorize("hasAnyRole('ADMIN', 'READ-ONLY','CUSTOMER')")
    @GetMapping("/rds")
    public ResponseEntity<List<RDSInstanceDTO>> getRdsInstances(@RequestParam("accountNumber") Long accountNumber) {
        List<RDSInstanceDTO> rdsInstances = awsService.fetchRDSInstances(accountNumber);
        if (rdsInstances.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(rdsInstances);
    }
    @PreAuthorize("hasAnyRole('ADMIN', 'READ-ONLY','CUSTOMER')")
    @GetMapping("/asg")
    public ResponseEntity<List<ASGDTO>> getAutoScalingGroups(@RequestParam("accountNumber") Long accountNumber) {
        List<ASGDTO> asgGroups = awsService.fetchAutoScalingGroups(accountNumber);
        if (asgGroups.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(asgGroups);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'READ-ONLY','CUSTOMER')")
    @GetMapping("/linked-accounts")
    public ResponseEntity<List<AssignAccountResponse>> getLinkedAccounts(@RequestParam("userId") Long userId){
        List<AssignAccountResponse> accounts = awsService.fetchLinkedAccounts(userId);
        return ResponseEntity.ok(accounts);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'READ-ONLY')")
    @GetMapping("/all-accounts")
    public ResponseEntity<List<AssignAccountResponse>> getAllAccounts(){
        List<AssignAccountResponse> accounts = awsService.fetchAllAccounts();
        return ResponseEntity.ok(accounts);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'READ-ONLY','CUSTOMER')")
    @GetMapping("/account-by-role")
    public ResponseEntity<List<AssignAccountResponse>> getAccountsByRole(){
        List<AssignAccountResponse> accounts = awsService.fetchAccountsByRole();
        return ResponseEntity.ok(accounts);
    }

}
