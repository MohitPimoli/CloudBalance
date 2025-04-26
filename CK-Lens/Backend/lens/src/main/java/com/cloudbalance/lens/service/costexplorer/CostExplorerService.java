package com.cloudbalance.lens.service.costexplorer;


import com.cloudbalance.lens.dto.costexplorer.CostExplorerRequestDTO;
import com.cloudbalance.lens.dto.costexplorer.CostExplorerResponseDTO;
import com.cloudbalance.lens.dto.costexplorer.DisplayNameDTO;

import java.util.List;

public interface CostExplorerService {


    List<String> getFilter(String columnName);

    List<DisplayNameDTO> getDisplayName();

    CostExplorerResponseDTO fetchDate(CostExplorerRequestDTO costExplorerRequestDTO);

}