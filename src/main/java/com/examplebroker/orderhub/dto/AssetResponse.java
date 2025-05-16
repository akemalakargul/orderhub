package com.examplebroker.orderhub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssetResponse {
    
    private Long id;
    private String customerId;
    private String assetName;
    private Long size;
    private Long usableSize;
} 