package com.cloudbalance.lens.controller;

import com.cloudbalance.lens.dto.account.AssignAccountResponse;
import com.cloudbalance.lens.dto.pagination.PagedResponse;
import com.cloudbalance.lens.dto.usermanagement.StatusDTO;
import com.cloudbalance.lens.dto.usermanagement.UserDTO;
import com.cloudbalance.lens.dto.usermanagement.UserManagementDTO;
import com.cloudbalance.lens.service.awsservices.impl.AWSServiceImpl;
import com.cloudbalance.lens.service.usermanagement.UserManagementService;
import com.cloudbalance.lens.validation.OnCreate;
import com.cloudbalance.lens.validation.OnUpdate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserManagementController {

    private final UserManagementService userManagementService;
    private final AWSServiceImpl awsService;


    public UserManagementController(UserManagementService userManagementService, AWSServiceImpl awsService) {
        this.userManagementService = userManagementService;
        this.awsService = awsService;
    }


    /**
     add a user*/

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register")
    public ResponseEntity<String> register(@Validated(OnCreate.class) @RequestBody UserManagementDTO userManagementDTO) {
        return ResponseEntity.ok(userManagementService.registerUser(userManagementDTO));
    }

    /**
     link Aws accounts to user*/

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update")
    public ResponseEntity<String> updateUser(@Validated(OnUpdate.class)  @RequestBody UserManagementDTO userManagementDTO) {
        return ResponseEntity.ok(userManagementService.updateUser(userManagementDTO));
    }

    /**
     fetch all users*/

    @PreAuthorize("hasAnyRole('ADMIN', 'READ-ONLY')")
    @GetMapping("/all")
    public ResponseEntity<PagedResponse<UserDTO>> fetchAllUsers(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok(userManagementService.fetchAllUsers(page, size));
    }

    /**
     fetch all Accounts*/

    @PreAuthorize("hasAnyRole('ADMIN', 'READ-ONLY')")
    @GetMapping("/account/all")
    public ResponseEntity<List<AssignAccountResponse>> fetchAllAccounts() {
        return ResponseEntity.ok(awsService.fetchAllAccounts());
    }

    /**
     fetch all Accounts by id*/

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all-accounts")
    public ResponseEntity<List<AssignAccountResponse>> fetchAllAccountsById(@RequestParam("userId") Long userId) {
        return ResponseEntity.ok(userManagementService.fetchAllAccounts(userId));
    }

    /**
     *  fetch users by id*/

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/get")
    public ResponseEntity<UserDTO> fetchUserDetail(@RequestParam("userId") Long userId){
        return ResponseEntity.ok(userManagementService.fetchUserDetail(userId));
    }

    /**
     fetch count of all and current active users*/

    @PreAuthorize("hasAnyRole('ADMIN', 'READ-ONLY')")
    @GetMapping("/status")
    public ResponseEntity<StatusDTO> fetchTotalUserStatus(){
        return ResponseEntity.ok(userManagementService.fetchStatus());
    }
}