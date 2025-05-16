package com.examplebroker.orderhub.repository;

import java.util.List;
import java.util.Optional;

import com.examplebroker.orderhub.model.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {
    
    List<Asset> findByCustomerId(String customerId);
    
    Optional<Asset> findByCustomerIdAndAssetName(String customerId, String assetName);
    
    boolean existsByCustomerIdAndAssetName(String customerId, String assetName);
}
