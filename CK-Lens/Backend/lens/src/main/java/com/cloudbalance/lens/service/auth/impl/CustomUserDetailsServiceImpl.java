package com.cloudbalance.lens.service.auth.impl;

import com.cloudbalance.lens.dto.auth.CustomUserDetails;
import com.cloudbalance.lens.entity.User;
import com.cloudbalance.lens.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return CustomUserDetails.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .role(user.getRole().getName())
                .build();
    }
}
