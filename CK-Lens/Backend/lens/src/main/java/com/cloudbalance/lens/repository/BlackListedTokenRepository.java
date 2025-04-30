package com.cloudbalance.lens.repository;

import com.cloudbalance.lens.entity.BlackListedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;

@Repository
public interface BlackListedTokenRepository extends JpaRepository<BlackListedToken, String> {

    /**
     * Check if a token is blacklisted.
     * @param token
     * @return if token exists or not
     */

    @Query("SELECT COUNT(b) > 0 FROM BlackListedToken b WHERE b.token = :token")
    boolean existsByToken(@Param("token")String token);

    /**
     * Delete all blacklisted tokens that have expired.
     * @param time
     */

    @Modifying
    @Query("DELETE FROM BlackListedToken b WHERE b.tokenExpiry < :time")
    void deleteAllByTokenExpiryBefore(@Param("time")LocalDateTime time);
}