//package com.cloudbalance.lens.service.costexplorer.impl;
//
//import com.cloudbalance.lens.dto.auth.CustomUserDetails;
//import com.cloudbalance.lens.dto.costexplorer.CostExplorerRequest;
//import com.cloudbalance.lens.dto.costexplorer.CostExplorerResponse;
//import com.cloudbalance.lens.entity.Account;
//import com.cloudbalance.lens.dto.account.AccountResponse;
//import com.cloudbalance.lens.entity.User;
//import com.cloudbalance.lens.entity.UserCloudAccount;
//import com.cloudbalance.lens.exception.ResourceNotFoundException;
//import com.cloudbalance.lens.exception.UnauthorizedException;
//import com.cloudbalance.lens.repository.UserRepository;
//import com.cloudbalance.lens.service.costexplorer.CostExplorerService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Service;
//import java.util.List;
//
//@Slf4j
//@Service
//public class CostExplorerServiceImpl implements CostExplorerService {
//
//    @Autowired
//    private UserRepository userRepository;
//    @Autowired
//    private AccountToAccountResponse accountToAccountResponse;
//
//
//    @Override
//    public AccountResponse linkedAccounts() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//        if (authentication == null || !authentication.isAuthenticated()) {
//            log.warn("Unauthenticated access attempt to linked accounts");
//            throw new UnauthorizedException("User is not authenticated");
//        }
//
//        Object principal = authentication.getPrincipal();
//        if (!(principal instanceof CustomUserDetails customUserDetails)) {
//            log.error("Authentication principal is not of expected type: {}", principal.getClass().getName());
//            throw new UnauthorizedException("Invalid authentication principal");
//        }
//
//        String username = customUserDetails.getUsername();
//        if (username == null) {
//            log.error("Authenticated user has null username");
//            throw new UnauthorizedException("Invalid user credentials");
//        }
//
//        log.info("Fetching linked accounts for user: {}", username);
//
//        User user = userRepository.findByUsername(username)
//                .orElseThrow(() -> {
//                    log.error("User not found in database: {}", username);
//                    return new ResourceNotFoundException("User not found");
//                });
//
//        List<UserCloudAccount> assignedAccounts = user.getAssignedAccounts();
//        if (assignedAccounts == null || assignedAccounts.isEmpty()) {
//            log.warn("User {} has no assigned cloud accounts", username);
//            return AccountResponse.builder()
//                    .linkedAccounts(List.of())
//                    .message("No linked accounts found.")
//                    .build();
//        }
//
//        List<Account> linkedAccounts = assignedAccounts.stream()
//                .map(UserCloudAccount::getCloudAccount)
//                .toList();
//
//        return accountToAccountResponse.toResponse(linkedAccounts);
//    }
//
//    @Override
//    public CostExplorerResponse getAccountCostDetail(CostExplorerRequest costExplorerRequest) {
//        // TODO: Implement logic here later
//        return null;
//    }
//}
