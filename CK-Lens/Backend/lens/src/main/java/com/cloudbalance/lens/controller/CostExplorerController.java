//package com.cloudbalance.lens.controller;
//
//import com.cloudbalance.lens.dto.account.AccountResponse;
//import com.cloudbalance.lens.dto.costexplorer.CostExplorerRequest;
//import com.cloudbalance.lens.dto.costexplorer.CostExplorerResponse;
//import com.cloudbalance.lens.service.costexplorer.CostExplorerService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/cost-explorer")
//public class CostExplorerController {
//
//    @Autowired
//    private CostExplorerService costExplorerService;
//
//    @GetMapping("/linked-accounts")
//    @PreAuthorize("isAuthenticated()")
//    public ResponseEntity<AccountResponse> linkedAccounts() {
//        AccountResponse response = costExplorerService.linkedAccounts();
//        System.out.println("Response: " + response);
//        return ResponseEntity.ok(response);
//    }
//    @GetMapping("/linked-account")
//    @PreAuthorize("isAuthenticated()")
//    public ResponseEntity<CostExplorerResponse> getAccountCostDetail(CostExplorerRequest costExplorerRequest) {
//        CostExplorerResponse costExplorerResponse = costExplorerService.getAccountCostDetail(costExplorerRequest);
//        return ResponseEntity.ok(costExplorerResponse);
//    }
//}
