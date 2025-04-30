package com.cloudbalance.lens.repository;

import com.cloudbalance.lens.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account,Long> {

    /**
     * Finds an account by its ARN or account number.
     * @param number
     * @return  List containing Account entity
     */

    @Query("SELECT a FROM Account a WHERE a.arn = :arn OR a.accountNumber = :number")
    List<Account> findByArnOrNumber(@Param("arn") String arn, @Param("number") Long number);

    /**
     * Finds an account by its ARN.
     * @param number
     * @return Optional of Account entity
     */

    @Query("SELECT a FROM Account a WHERE a.accountNumber = :number")
    Optional<Account> findByAccountNumber( @Param("number") Long number);


}

