package com.examplebroker.orderhub.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.examplebroker.orderhub.model.OrderStatus;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderFilterRequest {

    @NotBlank(message = "Customer ID is required")
    private String customerId;
    
    private LocalDate startDate;
    
    private LocalDate endDate;
    
    private OrderStatus status;
    
    public LocalDateTime getStartDateTime() {
        return startDate != null ? startDate.atStartOfDay() : null;
    }
    
    public LocalDateTime getEndDateTime() {
        return endDate != null ? endDate.atTime(LocalTime.MAX) : null;
    }
} 