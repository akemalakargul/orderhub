package com.examplebroker.orderhub.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.examplebroker.orderhub.dto.OrderFilterRequest;
import com.examplebroker.orderhub.dto.OrderRequest;
import com.examplebroker.orderhub.dto.OrderResponse;
import com.examplebroker.orderhub.exception.InvalidOrderStateException;
import com.examplebroker.orderhub.model.Asset;
import com.examplebroker.orderhub.model.Order;
import com.examplebroker.orderhub.model.OrderSide;
import com.examplebroker.orderhub.model.OrderStatus;
import com.examplebroker.orderhub.repository.OrderRepository;
import com.examplebroker.orderhub.service.impl.OrderServiceImpl;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    
    @Mock
    private OrderRepository orderRepository;
    
    @Mock
    private AssetService assetService;
    
    @InjectMocks
    private OrderServiceImpl orderService;
    
    private OrderRequest buyOrderRequest;
    private OrderRequest sellOrderRequest;
    private Order pendingOrder;
    private Order matchedOrder;
    private Asset tryAsset;
    private Asset stockAsset;
    
    @BeforeEach
    void setUp() {
        buyOrderRequest = OrderRequest.builder()
                .customerId("customer1")
                .assetName("AAPL")
                .orderSide(OrderSide.BUY)
                .size(5L)
                .price(new BigDecimal("150.00"))
                .build();
        
        sellOrderRequest = OrderRequest.builder()
                .customerId("customer1")
                .assetName("AAPL")
                .orderSide(OrderSide.SELL)
                .size(3L)
                .price(new BigDecimal("155.00"))
                .build();
        
        pendingOrder = Order.builder()
                .id(1L)
                .customerId("customer1")
                .assetName("AAPL")
                .orderSide(OrderSide.BUY)
                .size(5L)
                .price(new BigDecimal("150.00"))
                .status(OrderStatus.PENDING)
                .createDate(LocalDateTime.now())
                .build();
        
        matchedOrder = Order.builder()
                .id(2L)
                .customerId("customer1")
                .assetName("AAPL")
                .orderSide(OrderSide.SELL)
                .size(3L)
                .price(new BigDecimal("155.00"))
                .status(OrderStatus.MATCHED)
                .createDate(LocalDateTime.now().minusDays(1))
                .build();
        
        tryAsset = Asset.builder()
                .id(1L)
                .customerId("customer1")
                .assetName("TRY")
                .size(10000L)
                .usableSize(10000L)
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
    void createOrder_BuyOrder_Success() {
        // Arrange
        doNothing().when(assetService).createAssetIfNotExists(anyString(), anyString(), anyLong());
        when(assetService.updateAssetBalance(eq("customer1"), eq("TRY"), anyLong()))
                .thenReturn(tryAsset);
        when(orderRepository.save(any(Order.class))).thenReturn(pendingOrder);
        
        // Act
        OrderResponse result = orderService.createOrder(buyOrderRequest);
        
        // Assert
        assertNotNull(result);
        assertEquals(OrderSide.BUY, result.getOrderSide());
        assertEquals(OrderStatus.PENDING, result.getStatus());
        verify(assetService, times(1)).updateAssetBalance(
                eq("customer1"), eq("TRY"), eq(-750L)); // 5 shares * $150
    }
    
    @Test
    void createOrder_SellOrder_Success() {
        // Arrange
        doNothing().when(assetService).createAssetIfNotExists(anyString(), anyString(), anyLong());
        when(assetService.updateAssetBalance(eq("customer1"), eq("AAPL"), anyLong()))
                .thenReturn(stockAsset);
        when(orderRepository.save(any(Order.class))).thenReturn(
                Order.builder()
                    .id(3L)
                    .customerId("customer1")
                    .assetName("AAPL")
                    .orderSide(OrderSide.SELL)
                    .size(3L)
                    .price(new BigDecimal("155.00"))
                    .status(OrderStatus.PENDING)
                    .createDate(LocalDateTime.now())
                    .build());
        
        // Act
        OrderResponse result = orderService.createOrder(sellOrderRequest);
        
        // Assert
        assertNotNull(result);
        assertEquals(OrderSide.SELL, result.getOrderSide());
        assertEquals(OrderStatus.PENDING, result.getStatus());
        verify(assetService, times(1)).updateAssetBalance(
                eq("customer1"), eq("AAPL"), eq(-3L)); // 3 shares
    }
    
    @Test
    void getOrdersByFilter_AllOrders() {
        // Arrange
        OrderFilterRequest filterRequest = OrderFilterRequest.builder()
                .customerId("customer1")
                .build();
        
        when(orderRepository.findByCustomerId("customer1"))
                .thenReturn(Arrays.asList(pendingOrder, matchedOrder));
        
        // Act
        List<OrderResponse> result = orderService.getOrdersByFilter(filterRequest);
        
        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
    }
    
    @Test
    void getOrdersByFilter_ByDateRange() {
        // Arrange
        OrderFilterRequest filterRequest = OrderFilterRequest.builder()
                .customerId("customer1")
                .startDate(LocalDate.now().minusDays(2))
                .endDate(LocalDate.now())
                .build();
        
        when(orderRepository.findByCustomerIdAndCreateDateBetween(
                eq("customer1"), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Arrays.asList(pendingOrder, matchedOrder));
        
        // Act
        List<OrderResponse> result = orderService.getOrdersByFilter(filterRequest);
        
        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
    }
    
    @Test
    void getOrdersByFilter_ByStatus() {
        // Arrange
        OrderFilterRequest filterRequest = OrderFilterRequest.builder()
                .customerId("customer1")
                .status(OrderStatus.PENDING)
                .build();
        
        when(orderRepository.findByCustomerIdAndStatus("customer1", OrderStatus.PENDING))
                .thenReturn(Collections.singletonList(pendingOrder));
        
        // Act
        List<OrderResponse> result = orderService.getOrdersByFilter(filterRequest);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(OrderStatus.PENDING, result.get(0).getStatus());
    }
    
    @Test
    void cancelOrder_Success() {
        // Arrange
        when(orderRepository.findByIdAndStatus(1L, OrderStatus.PENDING))
                .thenReturn(Optional.of(pendingOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(
                Order.builder()
                    .id(1L)
                    .customerId("customer1")
                    .assetName("AAPL")
                    .orderSide(OrderSide.BUY)
                    .size(5L)
                    .price(new BigDecimal("150.00"))
                    .status(OrderStatus.CANCELED)
                    .createDate(pendingOrder.getCreateDate())
                    .build());
        
        // Act
        orderService.cancelOrder(1L);
        
        // Assert
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(assetService, times(1)).updateAssetBalance(
                eq("customer1"), eq("TRY"), eq(750L)); // Return 5 shares * $150
    }
    
    @Test
    void cancelOrder_OrderNotFound_ThrowsInvalidOrderStateException() {
        // Arrange
        when(orderRepository.findByIdAndStatus(999L, OrderStatus.PENDING))
                .thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(InvalidOrderStateException.class, () -> orderService.cancelOrder(999L));
    }
} 
