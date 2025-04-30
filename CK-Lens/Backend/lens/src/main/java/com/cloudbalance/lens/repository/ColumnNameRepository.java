package com.cloudbalance.lens.repository;

import com.cloudbalance.lens.dto.costexplorer.DisplayNameDTO;
import com.cloudbalance.lens.entity.ColumnName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ColumnNameRepository extends JpaRepository<ColumnName, String> {

    /**
     * Find a ColumnName entity by its field name.
     *
     * @param fieldName the field name of the column
     * @return an Optional containing the ColumnName entity if found, or empty if not found
     */

    @Query("SELECT c FROM ColumnName c WHERE c.fieldName = :fieldName")
    Optional<ColumnName> findByFieldName(@Param("fieldName") String fieldName);

    /**
     * Fetch displayName and fieldName
     * @return a List of DisplayNameDTO
     */

    @Query("SELECT new com.cloudbalance.lens.dto.costexplorer.DisplayNameDTO(c.displayName, c.fieldName) from ColumnName c ")
    List<DisplayNameDTO> getAllDisplayName();

}
