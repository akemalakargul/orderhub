package com.examplebroker.orderhub.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.examplebroker.orderhub.dto.AssetResponse;
import com.examplebroker.orderhub.exception.InsufficientBalanceException;
import com.examplebroker.orderhub.exception.ResourceNotFoundException;
import com.examplebroker.orderhub.model.Asset;
import com.examplebroker.orderhub.repository.AssetRepository;
import com.examplebroker.orderhub.service.impl.AssetServiceImpl;

@ExtendWith(MockitoExtension.class)
class AssetServiceTest {
    
    @Mock
    private AssetRepository assetRepository;
    
    @InjectMocks
    private AssetServiceImpl assetService;
    
    private Asset tryAsset;
    private Asset stockAsset;
    
    @BeforeEach
    void setUp() {
        tryAsset = Asset.builder()
                .id(1L)
                .customerId("customer1")
                .assetName("TRY")
                .size(1000L)
                .usableSize(1000L)
                .build();
        
        stockAsset = Asset.builder()
                .id(2L)
                .customerId("customer1")
                .assetName("AAPL")
                .size(10L)
                .usableSize(10L)
                .build();
    }
    
    @Test
    void getAssetsByCustomerId_ReturnsListOfAssets() {
        // Arrange
        when(assetRepository.findByCustomerId("customer1"))
                .thenReturn(Arrays.asList(tryAsset, stockAsset));
        
        // Act
        List<AssetResponse> result = assetService.getAssetsByCustomerId("customer1");
        
        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("TRY", result.get(0).getAssetName());
        assertEquals("AAPL", result.get(1).getAssetName());
    }
    
    @Test
    void getAssetByCustomerIdAndAssetName_ReturnsAsset() {
        // Arrange
        when(assetRepository.findByCustomerIdAndAssetName("customer1", "TRY"))
                .thenReturn(Optional.of(tryAsset));
        
        // Act
        AssetResponse result = assetService.getAssetByCustomerIdAndAssetName("customer1", "TRY");
        
        // Assert
        assertNotNull(result);
        assertEquals("TRY", result.getAssetName());
        assertEquals(1000L, result.getSize());
    }
    
    @Test
    void getAssetByCustomerIdAndAssetName_ThrowsResourceNotFoundException() {
        // Arrange
        when(assetRepository.findByCustomerIdAndAssetName("customer1", "UNKNOWN"))
                .thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, 
                () -> assetService.getAssetByCustomerIdAndAssetName("customer1", "UNKNOWN"));
    }
    
    @Test
    void updateAssetBalance_AddsBalance() {
        // Arrange
        when(assetRepository.findByCustomerIdAndAssetName("customer1", "TRY"))
                .thenReturn(Optional.of(tryAsset));
        when(assetRepository.save(tryAsset)).thenReturn(tryAsset);
        
        // Act
        Asset result = assetService.updateAssetBalance("customer1", "TRY", 500L);
        
        // Assert
        assertNotNull(result);
        assertEquals(1000L, result.getSize());
        assertEquals(1500L, result.getUsableSize());
    }
    
    @Test
    void updateAssetBalance_SubtractsBalance() {
        // Arrange
        when(assetRepository.findByCustomerIdAndAssetName("customer1", "TRY"))
                .thenReturn(Optional.of(tryAsset));
        when(assetRepository.save(tryAsset)).thenReturn(tryAsset);
        
        // Act
        Asset result = assetService.updateAssetBalance("customer1", "TRY", -500L);
        
        // Assert
        assertNotNull(result);
        assertEquals(1000L, result.getSize());
        assertEquals(500L, result.getUsableSize());
    }
    
    @Test
    void updateAssetBalance_ThrowsInsufficientBalanceException() {
        // Arrange
        when(assetRepository.findByCustomerIdAndAssetName("customer1", "TRY"))
                .thenReturn(Optional.of(tryAsset));
        
        // Act & Assert
        assertThrows(InsufficientBalanceException.class, 
                () -> assetService.updateAssetBalance("customer1", "TRY", -1500L));
    }
    
    @Test
    void createAssetIfNotExists_CreatesNewAsset() {
        // Arrange
        when(assetRepository.existsByCustomerIdAndAssetName("customer1", "BTC"))
                .thenReturn(false);
        
        // Act
        assetService.createAssetIfNotExists("customer1", "BTC", 0L);
        
        // Assert
        verify(assetRepository, times(1)).save(any(Asset.class));
    }
    
    @Test
    void createAssetIfNotExists_DoesNotCreateExistingAsset() {
        // Arrange
        when(assetRepository.existsByCustomerIdAndAssetName("customer1", "TRY"))
                .thenReturn(true);
        
        // Act
        assetService.createAssetIfNotExists("customer1", "TRY", 0L);
        
        // Assert
        verify(assetRepository, never()).save(any(Asset.class));
    }
} 
