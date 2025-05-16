package com.examplebroker.orderhub.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.examplebroker.orderhub.dto.AssetResponse;
import com.examplebroker.orderhub.exception.InsufficientBalanceException;
import com.examplebroker.orderhub.exception.ResourceNotFoundException;
import com.examplebroker.orderhub.model.Asset;
import com.examplebroker.orderhub.repository.AssetRepository;
import com.examplebroker.orderhub.service.AssetService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssetServiceImpl implements AssetService {
    
    private final AssetRepository assetRepository;
    
    @Override
    public List<AssetResponse> getAssetsByCustomerId(String customerId) {
        List<Asset> assets = assetRepository.findByCustomerId(customerId);
        return assets.stream()
                .map(this::mapToAssetResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public AssetResponse getAssetByCustomerIdAndAssetName(String customerId, String assetName) {
        Asset asset = assetRepository.findByCustomerIdAndAssetName(customerId, assetName)
                .orElseThrow(() -> new ResourceNotFoundException("Asset", "customerId and assetName", 
                        customerId + " and " + assetName));
        return mapToAssetResponse(asset);
    }
    
    @Override
    @Transactional
    public Asset updateAssetBalance(String customerId, String assetName, Long amount) {
        Asset asset = assetRepository.findByCustomerIdAndAssetName(customerId, assetName)
                .orElseThrow(() -> new ResourceNotFoundException("Asset", "customerId and assetName", 
                        customerId + " and " + assetName));
        
        if (amount < 0 && asset.getUsableSize() < Math.abs(amount)) {
            throw new InsufficientBalanceException(
                    "Insufficient balance for asset " + assetName + ". Required: " + Math.abs(amount) + 
                    ", Available: " + asset.getUsableSize());
        }

        //todo: why
        asset.setUsableSize(asset.getUsableSize() + amount);
        //todo: this should set on match
        //asset.setSize(asset.getSize() + amount);

        return assetRepository.save(asset);
    }

    @Override
    @Transactional
    public void createAssetIfNotExists(String customerId, String assetName, Long initialSize) {
        if (!assetRepository.existsByCustomerIdAndAssetName(customerId, assetName)) {
            Asset newAsset = Asset.builder()
                    .customerId(customerId)
                    .assetName(assetName)
                    .size(initialSize)
                    .usableSize(initialSize)
                    .build();
            assetRepository.save(newAsset);
            log.info("Created new asset: {} for customer: {}", assetName, customerId);
        }
    }

    //todo: maybe I can do mapStruct implementation or move these mapping operations to different class
    private AssetResponse mapToAssetResponse(Asset asset) {
        return AssetResponse.builder()
                .id(asset.getId())
                .customerId(asset.getCustomerId())
                .assetName(asset.getAssetName())
                .size(asset.getSize())
                .usableSize(asset.getUsableSize())
                .build();
    }
} 
