package com.cloudbalance.lens.service.usermanagement.impl;

import com.cloudbalance.lens.dto.account.AssignAccountResponse;
import com.cloudbalance.lens.dto.pagination.PagedResponse;
import com.cloudbalance.lens.dto.usermanagement.StatusDTO;
import com.cloudbalance.lens.dto.usermanagement.UserDTO;
import com.cloudbalance.lens.dto.usermanagement.UserManagementDTO;
import com.cloudbalance.lens.entity.Account;
import com.cloudbalance.lens.entity.Role;
import com.cloudbalance.lens.entity.User;
import com.cloudbalance.lens.entity.UserCloudAccount;
import com.cloudbalance.lens.exception.CustomException;
import com.cloudbalance.lens.exception.ResourceAlreadyExistsException;
import com.cloudbalance.lens.exception.ResourceNotFoundException;
import com.cloudbalance.lens.repository.AccountRepository;
import com.cloudbalance.lens.repository.RoleRepository;
import com.cloudbalance.lens.repository.UserCloudAccountRepository;
import com.cloudbalance.lens.repository.UserRepository;
import com.cloudbalance.lens.service.usermanagement.UserManagementService;
import com.cloudbalance.lens.utils.Constant;
import com.cloudbalance.lens.utils.PasswordDecryptorUtil;
import com.cloudbalance.lens.utils.PasswordEncoderUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class UserManagementImpl implements UserManagementService {


    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final UserCloudAccountRepository userCloudAccountRepository;
    private final PasswordDecryptorUtil passwordDecryptorUtil;

    public UserManagementImpl(UserRepository userRepository,
                              AccountRepository accountRepository,
                              RoleRepository roleRepository,
                              UserCloudAccountRepository userCloudAccountRepository,
                              PasswordDecryptorUtil passwordDecryptorUtil) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.roleRepository = roleRepository;
        this.userCloudAccountRepository = userCloudAccountRepository;
        this.passwordDecryptorUtil = passwordDecryptorUtil;
    }

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    @Transactional
    public String registerUser(UserManagementDTO userManagementDTO) {
        UserDTO userDTO = userManagementDTO.getUserDTO();

        List<User> existingUsers = userRepository.findByUsernameOrEmail(userDTO.getUsername(), userDTO.getEmail());
        if(!existingUsers.isEmpty()){
            throw new ResourceAlreadyExistsException("User already exist either username or email");
        }

        Role role = getRole(userDTO);
        String decryptedPass = passwordDecryptorUtil.decryptPassword(userDTO.getPassword());
        User user = User.builder()
                .firstname(userDTO.getFirstName())
                .lastname(userDTO.getLastName())
                .username(userDTO.getUsername())
                .email(userDTO.getEmail())
                .password(PasswordEncoderUtil.encode(decryptedPass))
                .role(role)
                .active(true)
                .build();

        user = userRepository.save(user);
        log.info("User '{}' registered successfully", user.getUsername());

        List<Long> accountIds = userManagementDTO.getAccountIds();
        if (accountIds != null && !accountIds.isEmpty()) {
            List<UserCloudAccount> userCloudAccounts = new ArrayList<>();
            for (Long accountId : accountIds) {
                Account account = accountRepository.findById(accountId)
                        .orElseThrow(() -> new ResourceNotFoundException("Account not found with ID: " + accountId));

                UserCloudAccount userCloudAccount = new UserCloudAccount();
                userCloudAccount.setUser(user);
                userCloudAccount.setCloudAccount(account);
                userCloudAccounts.add(userCloudAccount);
            }
            userCloudAccountRepository.saveAll(userCloudAccounts);
            log.info("{} account(s) linked successfully to user '{}'", accountIds.size(), user.getUsername());
        }
        return "Registration successful";
    }

    private Role getRole(UserDTO userDTO) {
        return roleRepository.findRoleIdByRoleName(userDTO.getRoleName())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with name: " + userDTO.getRoleName()));
    }


    @Override
    public String updateUser(UserManagementDTO userManagementDTO) {
        UserDTO userDTO = userManagementDTO.getUserDTO();
        User user = userRepository.findById(userDTO.getId())
                .orElseThrow(() -> new CustomException.UserNotFoundException("User not found with ID: " + userDTO.getId()));

        updateBasicFields(user, userDTO);
        updatePasswordIfNeeded(user, userDTO);
        updateRoleAndAccounts(user, userDTO, userManagementDTO.getAccountIds());

        userRepository.save(user);
        log.info("User ID {} updated successfully", user.getId());
        return "User details updated successfully";
    }

    private void updateBasicFields(User user, UserDTO userDTO) {
        if (userDTO.getFirstName() != null) user.setFirstname(userDTO.getFirstName());
        if (userDTO.getLastName() != null) user.setLastname(userDTO.getLastName());
        if (userDTO.getEmail() != null) user.setEmail(userDTO.getEmail());
    }

    private void updatePasswordIfNeeded(User user, UserDTO userDTO) {
        if (userDTO.getPassword() != null && !userDTO.getPassword().isBlank()) {
            String decryptedPass = passwordDecryptorUtil.decryptPassword(userDTO.getPassword());
            user.setPassword(PasswordEncoderUtil.encode(decryptedPass));
            log.info("Password updated for user ID {}", user.getId());
        }
    }

    private void updateRoleAndAccounts(User user, UserDTO userDTO, List<Long> accountIds) {
        if (userDTO.getRoleName() != null) {
            Role newRole = getRole(userDTO);
            user.setRole(newRole);

            if (!userDTO.getRoleName().equalsIgnoreCase("CUSTOMER")) {
                user.getAssignedAccounts().clear();
                log.info("Non-customer role detected. Cleared all account associations for user ID {}", user.getId());
            } else {
                updateAssignedAccounts(user, accountIds);
            }
        }
    }

    private void updateAssignedAccounts(User user, List<Long> accountIds) {
        if (accountIds != null) {
            user.getAssignedAccounts().clear();
            List<UserCloudAccount> newLinks = new ArrayList<>();
            for (Long accountId : accountIds) {
                Account account = accountRepository.findById(accountId)
                        .orElseThrow(() -> new ResourceNotFoundException("Account not found with ID: " + accountId));
                UserCloudAccount userCloudAccount = new UserCloudAccount();
                userCloudAccount.setUser(user);
                userCloudAccount.setCloudAccount(account);
                newLinks.add(userCloudAccount);
            }
            user.getAssignedAccounts().addAll(newLinks);
            log.info("{} account(s) linked to user ID {} as CUSTOMER", newLinks.size(), user.getId());
        }
    }


    @Override
    public PagedResponse<UserDTO> fetchAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> pagedUsers = userRepository.findAll(pageable);

        List<UserDTO> userDTOs = pagedUsers.stream()
                .map(user -> UserDTO.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .firstName(user.getFirstname())
                        .lastName(user.getLastname())
                        .email(user.getEmail())
                        .roleName(user.getRole().getName())
                        .active(user.isActive())
                        .lastLogin(user.getLastAccessedTime() != null
                                ? user.getLastAccessedTime().format(FORMATTER)
                                : null)
                        .build())
                .toList();
        log.info("All users fetched successfully");
        return new PagedResponse<>(
                userDTOs,
                pagedUsers.getNumber(),
                pagedUsers.getSize(),
                pagedUsers.getTotalElements(),
                pagedUsers.getTotalPages(),
                pagedUsers.isLast()
        );
    }

    @Override
    public List<AssignAccountResponse> fetchAllAccounts(Long id) {
        List<Account> allAccounts = accountRepository.findAll();
        List<AssignAccountResponse> assignAccountDTOS;
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new CustomException.UserNotFoundException(Constant.USER_NOT_FOUND_WITH_ID + id));
            List<Long> linkedAccountIds = user.getAssignedAccounts().stream()
                    .map(uca -> uca.getCloudAccount().getId())
                    .toList();

           assignAccountDTOS = allAccounts.stream().map(account -> {
                boolean isLinked = linkedAccountIds.contains(account.getId());
                return AssignAccountResponse.builder()
                        .accountId(account.getId())
                        .accountNumber(account.getAccountNumber())
                        .accountHolderName(account.getAccountHolderName())
                        .linked(isLinked)
                        .build();
            }).toList();
        log.info("All accounts fetched successfully");
        return assignAccountDTOS;
    }

    @Override
    public UserDTO fetchUserDetail(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException.UserNotFoundException(Constant.USER_NOT_FOUND_WITH_ID + id));
        log.info("User details fetched successfully");
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstname())
                .lastName(user.getLastname())
                .email(user.getEmail())
                .roleName(user.getRole().getName())
                .active(user.isActive())
                .lastLogin(user.getLastAccessedTime() != null
                        ? user.getLastAccessedTime().format(FORMATTER)
                        : null)
                .build();
    }

    @Override
    public StatusDTO fetchStatus() {
        long activeCount = userRepository.countByActiveTrue();
        long inactiveCount = userRepository.countByActiveFalse();
        log.info("ActiveInactive status count fetched successfully");
        return StatusDTO.builder()
                .active(activeCount)
                .all(inactiveCount+activeCount)
                .build();
    }
}
