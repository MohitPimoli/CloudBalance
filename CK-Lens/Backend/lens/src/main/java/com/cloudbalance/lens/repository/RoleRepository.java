package com.cloudbalance.lens.repository;

import com.cloudbalance.lens.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role,Long> {

    /**
     * Fetch role id from given roleName
     * @param roleName
     * @return Optional of Role entity
     */

    @Query("SELECT r FROM Role r WHERE r.name = :roleName")
    Optional<Role> findRoleIdByRoleName(@Param("roleName") String roleName);

}

