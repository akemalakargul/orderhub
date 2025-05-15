package com.examplebroker.orderhub.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "orders")
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Column(name = "customer_id", nullable = false)
    private String customerId;
    
    @NotBlank
    @Column(name = "asset_name", nullable = false)
    private String assetName;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "order_side", nullable = false)
    private OrderSide orderSide;
    
    @Positive
    @Column(nullable = false)
    private Long size;
    
    @Positive
    @Column(nullable = false)
    private BigDecimal price;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;
    
    @NotNull
    @Column(name = "create_date", nullable = false)
    private LocalDateTime createDate;
    
    @PrePersist
    protected void onCreate() {
        createDate = LocalDateTime.now();
        if (status == null) {
            status = OrderStatus.PENDING;
        }
    }
} 