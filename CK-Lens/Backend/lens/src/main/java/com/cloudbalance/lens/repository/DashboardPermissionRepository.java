package com.cloudbalance.lens.repository;

import com.cloudbalance.lens.entity.DashboardPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DashboardPermissionRepository extends JpaRepository<DashboardPermission,Long> {

    @Query("SELECT dp FROM DashboardPermission dp WHERE dp.role.name = :roleName")
    List<DashboardPermission> findByRoleName(@Param("roleName") String roleName);
}
