package com.examplebroker.orderhub.controller;

import com.examplebroker.orderhub.dto.OrderFilterRequest;
import com.examplebroker.orderhub.dto.OrderRequest;
import com.examplebroker.orderhub.dto.OrderResponse;
import com.examplebroker.orderhub.security.SecurityService;
import com.examplebroker.orderhub.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    
    private final OrderService orderService;
    private final SecurityService securityService;
    
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest orderRequest) {
        securityService.validateCustomerAccess(orderRequest.getCustomerId());
        log.info("Creating order for customer: {}", orderRequest.getCustomerId());
        OrderResponse createdOrder = orderService.createOrder(orderRequest);
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }
    
    @PostMapping("/filter")
    public ResponseEntity<List<OrderResponse>> getOrders(@Valid @RequestBody OrderFilterRequest filterRequest) {
        securityService.validateCustomerAccess(filterRequest.getCustomerId());
        log.info("Retrieving orders for customer: {}", filterRequest.getCustomerId());
        List<OrderResponse> orders = orderService.getOrdersByFilter(filterRequest);
        return ResponseEntity.ok(orders);
    }
    
    @DeleteMapping("/{orderId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER') and @orderService.isOrderOwnedByCustomer(#orderId, authentication.principal.customerId))")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long orderId) {
        log.info("Canceling order with ID: {}", orderId);
        orderService.cancelOrder(orderId);
        return ResponseEntity.noContent().build();
    }
} 
