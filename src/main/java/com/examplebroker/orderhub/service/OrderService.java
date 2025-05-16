package com.examplebroker.orderhub.service;

import com.examplebroker.orderhub.dto.OrderFilterRequest;
import com.examplebroker.orderhub.dto.OrderRequest;
import com.examplebroker.orderhub.dto.OrderResponse;

import java.util.List;

public interface OrderService {
    
    OrderResponse createOrder(OrderRequest orderRequest);
    
    List<OrderResponse> getOrdersByFilter(OrderFilterRequest filterRequest);
    
    void cancelOrder(Long orderId);
} 
