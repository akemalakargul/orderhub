package com.examplebroker.orderhub.controller;

import com.examplebroker.orderhub.dto.AssetResponse;
import com.examplebroker.orderhub.security.SecurityService;
import com.examplebroker.orderhub.service.AssetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/assets")
@RequiredArgsConstructor
@Slf4j
public class AssetController {
    
    private final AssetService assetService;
    private final SecurityService securityService;
    
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<AssetResponse>> getAssetsByCustomerId(@PathVariable String customerId) {
        securityService.validateCustomerAccess(customerId);
        log.info("Retrieving assets for customer: {}", customerId);
        List<AssetResponse> assets = assetService.getAssetsByCustomerId(customerId);
        return ResponseEntity.ok(assets);
    }

    @GetMapping("/customer/{customerId}/asset/{assetName}")
    public ResponseEntity<AssetResponse> getAssetByCustomerIdAndAssetName(
            @PathVariable String customerId, @PathVariable String assetName) {
        securityService.validateCustomerAccess(customerId);
        log.info("Retrieving asset: {} for customer: {}", assetName, customerId);
        AssetResponse asset = assetService.getAssetByCustomerIdAndAssetName(customerId, assetName);
        return ResponseEntity.ok(asset);
    }
} 
