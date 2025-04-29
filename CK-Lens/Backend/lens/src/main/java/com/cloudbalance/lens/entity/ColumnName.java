package com.cloudbalance.lens.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@Entity
@Table(name = "column_name")
@AllArgsConstructor
@NoArgsConstructor
public class ColumnName {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "column_name", nullable = false, unique = true)
    private String nameOfColumn;

    @Column(name = "display_name", nullable = false, unique = true)
    private String displayName;

    @Column(name = "field_name", nullable = false, unique = true)
    private String fieldName;

    @Builder.Default
    @Column(name = "visible")
    private boolean visible = true;
}
