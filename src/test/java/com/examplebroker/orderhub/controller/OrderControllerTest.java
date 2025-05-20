package com.examplebroker.orderhub.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import com.examplebroker.orderhub.config.SecurityConfig;
import com.examplebroker.orderhub.dto.OrderFilterRequest;
import com.examplebroker.orderhub.dto.OrderRequest;
import com.examplebroker.orderhub.dto.OrderResponse;
import com.examplebroker.orderhub.model.OrderSide;
import com.examplebroker.orderhub.model.OrderStatus;
import com.examplebroker.orderhub.security.JwtService;
import com.examplebroker.orderhub.security.SecurityService;
import com.examplebroker.orderhub.service.CustomerUserDetailsService;
import com.examplebroker.orderhub.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(OrderController.class)
@ContextConfiguration(classes = {OrderController.class, SecurityConfig.class})
@AutoConfigureMockMvc(addFilters = true)
class OrderControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private OrderService orderService;
    
    @MockBean
    private SecurityService securityService;
    
    @MockBean
    private CustomerUserDetailsService customerUserDetailsService;
    
    @MockBean
    private JwtService jwtService;
    
    private OrderRequest orderRequest;
    private OrderResponse orderResponse;
    private OrderFilterRequest filterRequest;
    private List<OrderResponse> orderResponses;
    
    @BeforeEach
    void setUp() {
        orderRequest = OrderRequest.builder()
                .customerId("customer1")
                .assetName("AAPL")
                .orderSide(OrderSide.BUY)
                .size(5L)
                .price(new BigDecimal("150.00"))
                .build();
        
        orderResponse = OrderResponse.builder()
                .id(1L)
                .customerId("customer1")
                .assetName("AAPL")
                .orderSide(OrderSide.BUY)
                .size(5L)
                .price(new BigDecimal("150.00"))
                .status(OrderStatus.PENDING)
                .createDate(LocalDateTime.now())
                .build();
        
        filterRequest = OrderFilterRequest.builder()
                .customerId("customer1")
                .build();
        
        orderResponses = Arrays.asList(
                orderResponse,
                OrderResponse.builder()
                        .id(2L)
                        .customerId("customer1")
                        .assetName("MSFT")
                        .orderSide(OrderSide.SELL)
                        .size(3L)
                        .price(new BigDecimal("300.00"))
                        .status(OrderStatus.MATCHED)
                        .createDate(LocalDateTime.now().minusDays(1))
                        .build()
        );
        
        doNothing().when(securityService).validateCustomerAccess("customer1");
        
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("mock.jwt.token");
        when(jwtService.isTokenValid(any(String.class), any(UserDetails.class))).thenReturn(true);
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void createOrder_ReturnsCreatedOrderResponse() throws Exception {
        when(orderService.createOrder(any(OrderRequest.class))).thenReturn(orderResponse);
        
        mockMvc.perform(post("/api/v1/orders")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.customerId").value("customer1"))
                .andExpect(jsonPath("$.assetName").value("AAPL"))
                .andExpect(jsonPath("$.orderSide").value("BUY"))
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.price").value(150.00))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void getOrders_ReturnsFilteredOrders() throws Exception {
        when(orderService.getOrdersByFilter(any(OrderFilterRequest.class))).thenReturn(orderResponses);
        
        mockMvc.perform(post("/api/v1/orders/filter")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(filterRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].customerId").value("customer1"))
                .andExpect(jsonPath("$[0].assetName").value("AAPL"))
                .andExpect(jsonPath("$[1].assetName").value("MSFT"));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void cancelOrder_ReturnsNoContent() throws Exception {
        doNothing().when(orderService).cancelOrder(1L);
        
        mockMvc.perform(delete("/api/v1/orders/1")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isNoContent());
    }
    
    @Test
    void createOrder_UnauthorizedWithoutAuthentication() throws Exception {
        mockMvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isUnauthorized());
    }
} 
