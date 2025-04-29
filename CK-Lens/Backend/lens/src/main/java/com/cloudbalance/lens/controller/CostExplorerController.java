package com.cloudbalance.lens.controller;

import com.cloudbalance.lens.dto.costexplorer.CostExplorerRequestDTO;
import com.cloudbalance.lens.dto.costexplorer.CostExplorerResponseDTO;
import com.cloudbalance.lens.dto.costexplorer.DisplayNameDTO;
import com.cloudbalance.lens.service.costexplorer.CostExplorerService;
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

    @PreAuthorize("hasAnyRole('ADMIN', 'READ-ONLY','CUSTOMER')")
    @GetMapping("/filter")
    public ResponseEntity<List<String>> getDistinctValues(@RequestParam("fieldName") String fieldName){   /// fetch Filter Values
        List<String> values = costExplorerService.getFilter(fieldName);
        return ResponseEntity.ok(values);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'READ-ONLY','CUSTOMER')")
    @GetMapping("/display-names")
    public ResponseEntity<List<DisplayNameDTO>> getDisplayName(){                                          /// fetch display name
        List<DisplayNameDTO> displayName = costExplorerService.getDisplayName();
        return ResponseEntity.ok(displayName);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'READ-ONLY','CUSTOMER')")                                           /// fetch data with or without filters
    @PostMapping("/data")
    public ResponseEntity<CostExplorerResponseDTO> getData(@RequestBody CostExplorerRequestDTO costExplorerRequestDTO) {
        return ResponseEntity.ok(costExplorerService.fetchDate(costExplorerRequestDTO));
    }

}