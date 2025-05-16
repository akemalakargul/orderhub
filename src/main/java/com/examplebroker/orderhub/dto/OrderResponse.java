package com.examplebroker.orderhub.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.examplebroker.orderhub.model.OrderSide;
import com.examplebroker.orderhub.model.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {
    
    private Long id;
    private String customerId;
    private String assetName;
    private OrderSide orderSide;
    private Long size;
    private BigDecimal price;
    private OrderStatus status;
    private LocalDateTime createDate;
} 