package com.examplebroker.orderhub.dto;

import java.math.BigDecimal;

import com.examplebroker.orderhub.model.OrderSide;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequest {
    
    @NotBlank(message = "Customer ID is required")
    private String customerId;
    
    @NotBlank(message = "Asset name is required")
    private String assetName;
    
    @NotNull(message = "Order side is required")
    private OrderSide orderSide;
    
    @Positive(message = "Size must be greater than zero")
    private Long size;
    
    @Positive(message = "Price must be greater than zero")
    private BigDecimal price;
} 