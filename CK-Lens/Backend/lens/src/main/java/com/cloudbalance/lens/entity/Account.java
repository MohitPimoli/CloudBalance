package com.cloudbalance.lens.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_number", nullable = false, unique = true)
    private Long accountNumber;

    @Column(name = "arn", nullable = false, unique = true)
    private String arn;

    @Column(name = "account_holder_name", nullable = false, unique = true)
    private String accountHolderName;

    @Builder.Default
    @Column(name = "orphan")
    private boolean orphan = true;

    @Builder.Default
    @Column(name = "account_region", nullable = false)
    private String accountRegion = "us-east-1";

    @OneToMany(mappedBy = "cloudAccount", fetch = FetchType.LAZY)
    private List<UserCloudAccount> assignedUsers;
}

