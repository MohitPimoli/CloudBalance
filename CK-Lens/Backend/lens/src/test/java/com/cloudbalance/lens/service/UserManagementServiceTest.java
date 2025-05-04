package com.cloudbalance.lens.service;

import com.cloudbalance.lens.dto.account.AssignAccountResponse;
import com.cloudbalance.lens.dto.pagination.PagedResponse;
import com.cloudbalance.lens.dto.usermanagement.StatusDTO;
import com.cloudbalance.lens.dto.usermanagement.UserDTO;
import com.cloudbalance.lens.dto.usermanagement.UserManagementDTO;
import com.cloudbalance.lens.entity.Account;
import com.cloudbalance.lens.entity.Role;
import com.cloudbalance.lens.entity.User;
import com.cloudbalance.lens.entity.UserCloudAccount;
import com.cloudbalance.lens.exception.ResourceAlreadyExistsException;
import com.cloudbalance.lens.repository.AccountRepository;
import com.cloudbalance.lens.repository.RoleRepository;
import com.cloudbalance.lens.repository.UserCloudAccountRepository;
import com.cloudbalance.lens.repository.UserRepository;
import com.cloudbalance.lens.service.usermanagement.impl.UserManagementImpl;
import com.cloudbalance.lens.utils.PasswordDecryptorUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserManagementServiceTest {

    @InjectMocks
    private UserManagementImpl userManagement;

    @Mock private UserRepository userRepository;
    @Mock private AccountRepository accountRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private UserCloudAccountRepository userCloudAccountRepository;
    @Mock private PasswordDecryptorUtil passwordDecryptorUtil;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerUser_shouldRegisterSuccessfully() {

        UserDTO userDTO = UserDTO.builder()
                .username("john")
                .email("john@example.com")
                .password("encryptedPass")
                .firstName("John")
                .lastName("Doe")
                .roleName("CUSTOMER")
                .build();
        UserManagementDTO dto = UserManagementDTO.builder()
                .userDTO(userDTO)
                .accountIds(List.of(1L, 2L))
                .build();

        when(userRepository.findByUsernameOrEmail("john", "john@example.com")).thenReturn(Collections.emptyList());
        when(roleRepository.findRoleIdByRoleName("CUSTOMER")).thenReturn(Optional.of(Role.builder().name("CUSTOMER").id(1L).build()));
        when(passwordDecryptorUtil.decryptPassword("encryptedPass")).thenReturn("plainPass");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);
        when(accountRepository.findById(anyLong())).thenReturn(Optional.of(Account.builder().id(1L).build()));
        String result = userManagement.registerUser(dto);
        assertThat(result).isEqualTo("Registration successful");

        verify(userCloudAccountRepository, times(1)).saveAll(anyList());
    }

    @Test
    void registerUser_shouldThrowWhenUserExists() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("john");
        userDTO.setEmail("john@example.com");

        UserManagementDTO dto = UserManagementDTO.builder().userDTO(userDTO).build();

        when(userRepository.findByUsernameOrEmail("john", "john@example.com")).thenReturn(List.of(new User()));

        assertThatThrownBy(() -> userManagement.registerUser(dto))
                .isInstanceOf(ResourceAlreadyExistsException.class);
    }

    @Test
    void updateUser_shouldUpdateSuccessfully() {
        UserDTO userDTO = UserDTO.builder()
                .id(1L)
                .firstName("Jane")
                .lastName("Smith")
                .email("jane@example.com")
                .password("enc")
                .roleName("CUSTOMER")
                .build();

        UserManagementDTO dto = UserManagementDTO.builder()
                .userDTO(userDTO)
                .accountIds(List.of(1L, 2L))
                .build();

        User existingUser = User.builder()
                .id(1L)
                .role(Role.builder().name("CUSTOMER").id(1L).build())
                .assignedAccounts(new ArrayList<>())
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(roleRepository.findRoleIdByRoleName("CUSTOMER")).thenReturn(Optional.of(Role.builder().name("CUSTOMER").id(1L).build()));
        when(passwordDecryptorUtil.decryptPassword("enc")).thenReturn("pass");

        String result = userManagement.updateUser(dto);

        assertThat(result).isEqualTo("User details updated successfully");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void fetchAllUsers_shouldReturnPagedUsers() {
        User user = User.builder()
                .id(1L)
                .username("john")
                .firstname("John")
                .lastname("Doe")
                .email("john@example.com")
                .role(Role.builder().name("ADMIN").id(1L).build())
                .active(true)
                .lastAccessedTime(LocalDateTime.now())
                .build();

        Page<User> page = new PageImpl<>(List.of(user));
        when(userRepository.findAll(any(Pageable.class))).thenReturn(page);

        PagedResponse<UserDTO> response = userManagement.fetchAllUsers(0, 10);

        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getTotalElements()).isEqualTo(1);
    }

    @Test
    void fetchAllAccounts_shouldReturnLinkedStatus() {
        User user = User.builder()
                .id(1L)
                .assignedAccounts(List.of(
                        UserCloudAccount.builder().cloudAccount(Account.builder().id(2L).build()).build()
                ))
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(accountRepository.findAll()).thenReturn(List.of(
                Account.builder().id(2L).accountNumber(123L).accountHolderName("John").build(),
                Account.builder().id(3L).accountNumber(456L).accountHolderName("Jane").build()
        ));

        List<AssignAccountResponse> accounts = userManagement.fetchAllAccounts(1L);
        assertThat(accounts).hasSize(2);
        assertThat(accounts.get(0).isLinked()).isTrue();
        assertThat(accounts.get(1).isLinked()).isFalse();
    }

    @Test
    void fetchUserDetail_shouldReturnUserDTO() {
        User user = User.builder()
                .id(1L)
                .username("john")
                .firstname("John")
                .lastname("Doe")
                .email("john@example.com")
                .role(Role.builder().name("ADMIN").id(1L).build())
                .active(true)
                .lastAccessedTime(LocalDateTime.now())
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDTO dto = userManagement.fetchUserDetail(1L);
        assertThat(dto.getUsername()).isEqualTo("john");
        assertThat(dto.getRoleName()).isEqualTo("ADMIN");
    }

    @Test
    void fetchStatus_shouldReturnCorrectCounts() {
        when(userRepository.countByActiveTrue()).thenReturn(5L);
        when(userRepository.countByActiveFalse()).thenReturn(2L);

        StatusDTO status = userManagement.fetchStatus();
        assertThat(status.getActive()).isEqualTo(5);
        assertThat(status.getAll()).isEqualTo(7);
    }
}
