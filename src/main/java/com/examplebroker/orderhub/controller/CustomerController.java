package com.examplebroker.orderhub.controller;

import com.examplebroker.orderhub.model.Customer;
import com.examplebroker.orderhub.service.CustomerUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Slf4j
public class CustomerController {

    private final CustomerUserDetailsService customerUserDetailsService;

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentCustomer(@AuthenticationPrincipal Object principal) {
        Map<String, Object> response = new HashMap<>();
        
        if (principal instanceof Customer) {
            Customer customer = (Customer) principal;
            response.put("customerId", customer.getCustomerId());
            response.put("username", customer.getUsername());
            response.put("email", customer.getEmail());
            response.put("fullName", customer.getFullName());
            response.put("role", "CUSTOMER");
        } else {
            response.put("username", "admin");
            response.put("role", "ADMIN");
        }
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{customerId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER') and authentication.principal.customerId == #customerId)")
    public ResponseEntity<Map<String, Object>> getCustomerById(@PathVariable String customerId) {
        log.info("Retrieving customer details for customerId: {}", customerId);
        
        UserDetails userDetails = customerUserDetailsService.loadUserByCustomerId(customerId);
        Map<String, Object> response = new HashMap<>();
        
        if (userDetails instanceof Customer customer) {
            response.put("customerId", customer.getCustomerId());
            response.put("username", customer.getUsername());
            response.put("email", customer.getEmail());
            response.put("fullName", customer.getFullName());
            response.put("role", "CUSTOMER");
        }
        
        return ResponseEntity.ok(response);
    }
} 
