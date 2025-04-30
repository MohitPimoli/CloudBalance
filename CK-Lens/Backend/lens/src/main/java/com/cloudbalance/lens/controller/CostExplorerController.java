package com.cloudbalance.lens.controller;

import com.cloudbalance.lens.dto.costexplorer.CostExplorerRequestDTO;
import com.cloudbalance.lens.dto.costexplorer.CostExplorerResponseDTO;
import com.cloudbalance.lens.dto.costexplorer.DisplayNameDTO;
import com.cloudbalance.lens.service.costexplorer.CostExplorerService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/cost")
public class CostExplorerController {

    private final CostExplorerService costExplorerService;
    public CostExplorerController(CostExplorerService costExplorerService) {
        this.costExplorerService = costExplorerService;
    }

    /**
    fetch Filter Values
    */
    @PreAuthorize("hasAnyRole('ADMIN', 'READ-ONLY','CUSTOMER')")
    @GetMapping("/filter")
    public ResponseEntity<List<String>> getDistinctValues(@RequestParam("fieldName") String fieldName){
        List<String> values = costExplorerService.getFilter(fieldName);
        return ResponseEntity.ok(values);
    }

    /**
     fetch display name
     */

    @PreAuthorize("hasAnyRole('ADMIN', 'READ-ONLY','CUSTOMER')")
    @GetMapping("/display-names")
    public ResponseEntity<List<DisplayNameDTO>> getDisplayName(){
        List<DisplayNameDTO> displayName = costExplorerService.getDisplayName();
        return ResponseEntity.ok(displayName);
    }

    /**
     fetch data with or without filters
     */

    @PreAuthorize("hasAnyRole('ADMIN', 'READ-ONLY','CUSTOMER')")
    @PostMapping("/data")
    public ResponseEntity<CostExplorerResponseDTO> getData(@Valid @RequestBody CostExplorerRequestDTO costExplorerRequestDTO) {
        return ResponseEntity.ok(costExplorerService.fetchDate(costExplorerRequestDTO));
    }

}