package com.cloudbalance.lens.repository;

import com.cloudbalance.lens.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    /**
     * Fetch User entity by username
     * @param username
     * @return Optional of User entity
     */

    @Query("SELECT u FROM User u JOIN u.role r WHERE u.username = :username")
    Optional<User> findByUsername(@Param("username") String username);

    /**
     * Fetch all User by username and email
     * @param username
     * @param email
     * @return List of User entity
     */

    @Query("SELECT u FROM User u WHERE u.username = :username OR u.email = :email")
    List<User> findByUsernameOrEmail(@Param("username") String username, @Param("email") String email);

    long countByActiveTrue();
    long countByActiveFalse();
}
