package com.cloudbalance.lens.controller;

import com.cloudbalance.lens.dto.account.AssignAccountResponse;
import com.cloudbalance.lens.dto.pagination.PagedResponse;
import com.cloudbalance.lens.dto.usermanagement.StatusDTO;
import com.cloudbalance.lens.dto.usermanagement.UserDTO;
import com.cloudbalance.lens.dto.usermanagement.UserManagementDTO;
import com.cloudbalance.lens.service.awsservices.impl.AWSServiceImpl;
import com.cloudbalance.lens.service.usermanagement.UserManagementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(UserManagementController.class)
class UserManagementControllerTest {

    @Mock
    private UserManagementService userManagementService;

    @Mock
    private AWSServiceImpl awsService;

    @InjectMocks
    private UserManagementController userManagementController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerUser_returnsSuccessMessage() {
        UserManagementDTO dto = UserManagementDTO.builder().userDTO(UserDTO.builder().username("john").build()).build();
        when(userManagementService.registerUser(dto)).thenReturn("User registered");

        ResponseEntity<String> response = userManagementController.register(dto);

        assertThat(response.getBody()).isEqualTo("User registered");
        verify(userManagementService).registerUser(dto);
    }

    @Test
    void updateUser_returnsSuccessMessage() {
        UserManagementDTO dto = UserManagementDTO.builder().userDTO(UserDTO.builder().id(1L).build()).build();
        when(userManagementService.updateUser(dto)).thenReturn("User updated");

        ResponseEntity<String> response = userManagementController.updateUser(dto);

        assertThat(response.getBody()).isEqualTo("User updated");
        verify(userManagementService).updateUser(dto);
    }

    @Test
    void fetchAllUsers_returnsPagedUsers() {
        PagedResponse<UserDTO> pagedResponse = new PagedResponse<>();
        when(userManagementService.fetchAllUsers(0, 10)).thenReturn(pagedResponse);

        ResponseEntity<PagedResponse<UserDTO>> response = userManagementController.fetchAllUsers(0, 10);

        assertThat(response.getBody()).isEqualTo(pagedResponse);
        verify(userManagementService).fetchAllUsers(0, 10);
    }

    @Test
    void fetchAllAccounts_returnsAccountList() {
        List<AssignAccountResponse> mockAccounts = List.of(
                AssignAccountResponse.builder()
                        .accountId(1L)
                        .accountNumber(123456789012L)
                        .accountHolderName("Test Account")
                        .linked(true)
                        .build()
        );

        when(awsService.fetchAllAccounts()).thenReturn(mockAccounts);

        ResponseEntity<List<AssignAccountResponse>> response = userManagementController.fetchAllAccounts();

        assertThat(response.getBody()).isEqualTo(mockAccounts);
        verify(awsService).fetchAllAccounts();
    }

    @Test
    void fetchAllAccountsById_returnsAccounts() {
        Long userId = 1L;
        List<AssignAccountResponse> mockAccounts = List.of(
                AssignAccountResponse.builder()
                        .accountId(1L)
                        .accountNumber(123456789012L)
                        .accountHolderName("Test Account")
                        .linked(true)
                        .build()
        );

        when(userManagementService.fetchAllAccounts(userId)).thenReturn(mockAccounts);

        ResponseEntity<List<AssignAccountResponse>> response = userManagementController.fetchAllAccountsById(userId);

        assertThat(response.getBody()).isEqualTo(mockAccounts);
        verify(userManagementService).fetchAllAccounts(userId);
    }

    @Test
    void fetchUserDetail_returnsUserDTO() {
        Long userId = 1L;
        UserDTO userDTO = UserDTO.builder().id(userId).build();
        when(userManagementService.fetchUserDetail(userId)).thenReturn(userDTO);

        ResponseEntity<UserDTO> response = userManagementController.fetchUserDetail(userId);

        assertThat(response.getBody()).isEqualTo(userDTO);
        verify(userManagementService).fetchUserDetail(userId);
    }



    @Test
    void fetchTotalUserStatus_returnsStatusDTO() {
        StatusDTO statusDTO =  StatusDTO.builder()
                .active(50L)
                .all(10L)
                .build();
        when(userManagementService.fetchStatus()).thenReturn(statusDTO);

        ResponseEntity<StatusDTO> response = userManagementController.fetchTotalUserStatus();

        assertThat(response.getBody()).isEqualTo(statusDTO);
        verify(userManagementService).fetchStatus();
    }
}
