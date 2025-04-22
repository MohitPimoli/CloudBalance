package com.cloudbalance.lens.repository;

import com.cloudbalance.lens.entity.UserCloudAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCloudAccountRepository extends JpaRepository<UserCloudAccount,Long> {
}
