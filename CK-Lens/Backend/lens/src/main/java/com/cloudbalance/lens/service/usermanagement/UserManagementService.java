package com.cloudbalance.lens.service.usermanagement;

import com.cloudbalance.lens.dto.account.AssignAccountResponse;
import com.cloudbalance.lens.dto.pagination.PagedResponse;
import com.cloudbalance.lens.dto.usermanagement.StatusDTO;
import com.cloudbalance.lens.dto.usermanagement.UserDTO;
import com.cloudbalance.lens.dto.usermanagement.UserManagementDTO;

import java.util.List;

public interface UserManagementService {
    String registerUser(UserManagementDTO userManagementDTO);
    String updateUser(UserManagementDTO userManagementDTO);
    PagedResponse<UserDTO> fetchAllUsers(int page, int size);
    List<AssignAccountResponse> fetchAllAccounts(Long id);
    UserDTO fetchUserDetail(Long id);
    StatusDTO fetchStatus();
}
