package com.examplebroker.orderhub.service.impl;

import com.examplebroker.orderhub.dto.OrderFilterRequest;
import com.examplebroker.orderhub.dto.OrderRequest;
import com.examplebroker.orderhub.dto.OrderResponse;
import com.examplebroker.orderhub.exception.InvalidOrderStateException;
import com.examplebroker.orderhub.model.Order;
import com.examplebroker.orderhub.model.OrderSide;
import com.examplebroker.orderhub.model.OrderStatus;
import com.examplebroker.orderhub.repository.OrderRepository;
import com.examplebroker.orderhub.service.AssetService;
import com.examplebroker.orderhub.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {
    
    private final OrderRepository orderRepository;
    private final AssetService assetService;
    private static final String TRY_ASSET = "TRY";
    
    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest orderRequest) {
        // Ensure the customer has an asset record for the asset being traded
        assetService.createAssetIfNotExists(orderRequest.getCustomerId(), orderRequest.getAssetName(), 0L);
        
        // Ensure the customer has a TRY asset record
        assetService.createAssetIfNotExists(orderRequest.getCustomerId(), TRY_ASSET, 0L);
        
        // Calculate the total order value
        BigDecimal totalOrderValue = orderRequest.getPrice()
                .multiply(BigDecimal.valueOf(orderRequest.getSize()));

        //todo: should we apply MATCHED case
        // Check and update balances based on order side
        if (orderRequest.getOrderSide() == OrderSide.BUY) {
            Long tryAmount = totalOrderValue.longValue() * -1; // Negative amount to reduce balance
            assetService.updateAssetBalance(orderRequest.getCustomerId(), TRY_ASSET, tryAmount);
            //todo: should not we update STOCK asset amount
        } else {
            Long assetAmount = orderRequest.getSize() * -1; // Negative amount to reduce balance
            assetService.updateAssetBalance(
                    orderRequest.getCustomerId(), orderRequest.getAssetName(), assetAmount);
            //todo: should not we update TRY asset amount
        }
        
        Order order = Order.builder()
                .customerId(orderRequest.getCustomerId())
                .assetName(orderRequest.getAssetName())
                .orderSide(orderRequest.getOrderSide())
                .size(orderRequest.getSize())
                .price(orderRequest.getPrice())
                .status(OrderStatus.PENDING)
                .createDate(LocalDateTime.now())
                .build();
        
        Order savedOrder = orderRepository.save(order);
        log.info("Created new order: {} for customer: {}", savedOrder.getId(), savedOrder.getCustomerId());
        
        return mapToOrderResponse(savedOrder);
    }

    //todo: cannot make both filtering maybe i should add validation
    //todo: I should show deleted orders probably
    @Override
    public List<OrderResponse> getOrdersByFilter(OrderFilterRequest filterRequest) {
        List<Order> orders;
        
        if (filterRequest.getStartDate() != null && filterRequest.getEndDate() != null) {
            orders = orderRepository.findByCustomerIdAndCreateDateBetween(
                    filterRequest.getCustomerId(), 
                    filterRequest.getStartDateTime(), 
                    filterRequest.getEndDateTime());
        } else if (filterRequest.getStatus() != null) {
            orders = orderRepository.findByCustomerIdAndStatus(
                    filterRequest.getCustomerId(), filterRequest.getStatus());
        } else {
            orders = orderRepository.findByCustomerId(filterRequest.getCustomerId());
        }
        
        return orders.stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findByIdAndStatus(orderId, OrderStatus.PENDING)
                .orElseThrow(() -> new InvalidOrderStateException(
                        "Order with ID " + orderId + " not found or not in PENDING state"));
        
        order.setStatus(OrderStatus.CANCELED);
        orderRepository.save(order);

        //todo : should we return or should we deducted when approved
        // Return funds to customer
        if (order.getOrderSide() == OrderSide.BUY) {
            // Return TRY to customer for BUY orders
            BigDecimal totalOrderValue = order.getPrice().multiply(BigDecimal.valueOf(order.getSize()));
            Long tryAmount = totalOrderValue.longValue();
            assetService.updateAssetBalance(order.getCustomerId(), TRY_ASSET, tryAmount);
        } else {
            // Return asset to customer for SELL orders
            Long assetAmount = order.getSize(); // Positive amount to add balance back
            assetService.updateAssetBalance(order.getCustomerId(), order.getAssetName(), assetAmount);
        }
        
        log.info("Canceled order: {} for customer: {}", orderId, order.getCustomerId());
    }
    
    private OrderResponse mapToOrderResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .customerId(order.getCustomerId())
                .assetName(order.getAssetName())
                .orderSide(order.getOrderSide())
                .size(order.getSize())
                .price(order.getPrice())
                .status(order.getStatus())
                .createDate(order.getCreateDate())
                .build();
    }
} 
