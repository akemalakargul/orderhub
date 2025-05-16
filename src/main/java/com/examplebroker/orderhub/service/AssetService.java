package com.examplebroker.orderhub.service;

import java.util.List;

import com.examplebroker.orderhub.dto.AssetResponse;
import com.examplebroker.orderhub.model.Asset;

public interface AssetService {
    
    List<AssetResponse> getAssetsByCustomerId(String customerId);
    
    AssetResponse getAssetByCustomerIdAndAssetName(String customerId, String assetName);
    
    Asset updateAssetBalance(String customerId, String assetName, Long amount);
    
    void createAssetIfNotExists(String customerId, String assetName, Long initialSize);
} 