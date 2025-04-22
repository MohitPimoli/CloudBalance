package com.cloudbalance.lens.controller;

import com.cloudbalance.lens.dto.account.AssignAccountResponse;
import com.cloudbalance.lens.dto.pagination.PagedResponse;
import com.cloudbalance.lens.dto.usermanagement.UserDTO;
import com.cloudbalance.lens.dto.usermanagement.UserManagementDTO;
import com.cloudbalance.lens.service.awsservices.impl.AWSServiceImpl;
import com.cloudbalance.lens.service.usermanagement.UserManagementService;
import com.cloudbalance.lens.validation.OnCreate;
import com.cloudbalance.lens.validation.OnUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserManagementController {

    @Autowired
    private UserManagementService userManagementService;
    @Autowired
    private AWSServiceImpl awsService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register")                                                            //add a user
    public ResponseEntity<String> register(@Validated(OnCreate.class) @RequestBody UserManagementDTO userManagementDTO) {
        return ResponseEntity.ok(userManagementService.registerUser(userManagementDTO));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update")                                                                   // link Aws accounts to user
    public ResponseEntity<String> updateUser(@Validated(OnUpdate.class)  @RequestBody UserManagementDTO userManagementDTO) {
        return ResponseEntity.ok(userManagementService.updateUser(userManagementDTO));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'READ-ONLY')")
    @GetMapping("/all")
    public ResponseEntity<PagedResponse<UserDTO>> fetchAllUsers(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok(userManagementService.fetchAllUsers(page, size));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'READ-ONLY')")
    @GetMapping("/account/all")                                                                                  // fetch all Accounts
    public ResponseEntity<List<AssignAccountResponse>> fetchAllAccounts() {
        return ResponseEntity.ok(awsService.fetchAllAccounts());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all-accounts")                                                                      // fetch all Accounts by id
    public ResponseEntity<List<AssignAccountResponse>> fetchAllAccountsById(@RequestParam("userId") Long userId) {
        return ResponseEntity.ok(userManagementService.fetchAllAccounts(userId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/get")                                                                                 // fetch users by id
    public ResponseEntity<UserDTO> fetchUserDetail(@RequestParam("userId") Long userId){
        return ResponseEntity.ok(userManagementService.fetchUserDetail(userId));
    }
}