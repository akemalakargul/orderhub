package com.examplebroker.orderhub.repository;

import com.examplebroker.orderhub.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

} 
