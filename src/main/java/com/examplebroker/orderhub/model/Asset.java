package com.examplebroker.orderhub.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "assets", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"customer_id", "asset_name"})
})
public class Asset {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Column(name = "customer_id", nullable = false)
    private String customerId;
    
    @NotBlank
    @Column(name = "asset_name", nullable = false)
    private String assetName;
    
    @Min(0)
    @Column(nullable = false)
    private Long size;
    
    @Min(0)
    @Column(name = "usable_size", nullable = false)
    private Long usableSize;
} 