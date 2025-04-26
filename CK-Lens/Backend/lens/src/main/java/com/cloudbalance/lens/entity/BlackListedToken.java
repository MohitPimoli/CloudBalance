package com.cloudbalance.lens.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Builder
@Entity
@Data
@Table(name = "black_listed_token")
public class BlackListedToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 512, nullable = false, unique = true)
    private String token;

    @Column(name="blacklisted_at")
    private LocalDateTime blacklistedAt;

    @Column(name="token_expiry")
    private LocalDateTime tokenExpiry;

    @PrePersist
    protected void onCreate() {
        this.blacklistedAt = LocalDateTime.now().withNano(0);
    }
}
