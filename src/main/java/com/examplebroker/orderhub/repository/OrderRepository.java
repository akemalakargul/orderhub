package com.examplebroker.orderhub.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.examplebroker.orderhub.model.Order;
import com.examplebroker.orderhub.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    List<Order> findByCustomerId(String customerId);
    
    List<Order> findByCustomerIdAndCreateDateBetween(String customerId, LocalDateTime startDate, LocalDateTime endDate);
    
    List<Order> findByCustomerIdAndStatus(String customerId, OrderStatus status);
    
    Optional<Order> findByIdAndStatus(Long id, OrderStatus status);
} 
