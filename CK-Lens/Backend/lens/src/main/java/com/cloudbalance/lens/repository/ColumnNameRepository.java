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

    @Query("SELECT c FROM ColumnName c WHERE c.columnName = :columnName")
    Optional<ColumnName> findByColumnName(@Param("columnName") String columnName);

    @Query("SELECT c FROM ColumnName c WHERE c.columnName IN :columnNames")
    List<ColumnName> findByColumnNames(@Param("columnNames") List<String> columnNames);

    @Query("SELECT c FROM ColumnName c")
    List<ColumnName> findAllColumnNames();

    @Query("SELECT c FROM ColumnName c WHERE c.fieldName = :fieldName")
    Optional<ColumnName> findByFieldName(@Param("fieldName") String fieldName);

    @Query("SELECT new com.cloudbalance.lens.dto.costexplorer.DisplayNameDTO(c.displayName, c.fieldName) from ColumnName c ")
    List<DisplayNameDTO> getAllDisplayName();

}
