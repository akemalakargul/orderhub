package com.examplebroker.orderhub.security;

import com.examplebroker.orderhub.model.Customer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

@Service
@RequestScope
@RequiredArgsConstructor
@Slf4j
public class SecurityService {

    private static final String ROLE_ADMIN = "ROLE_ADMIN";
    private static final String ROLE_CUSTOMER = "ROLE_CUSTOMER";

    private final Authentication authentication;

    public void validateCustomerAccess(String requestedCustomerId) {
        if (authentication == null) {
            log.warn("Access denied - no authentication found");
            throw new AccessDeniedException("Access denied. No authentication found.");
        }
        
        if (hasRole(ROLE_ADMIN)) {
            log.debug("Admin access granted for customer: {}", requestedCustomerId);
            return;
        }
        
        if (hasRole(ROLE_CUSTOMER) && authentication.getPrincipal() instanceof CustomerUserDetails userDetails) {
            Customer customer = userDetails.getCustomer();
            
            if (!customer.getCustomerId().equals(requestedCustomerId)) {
                log.warn("Access denied for customer {} trying to access data for customer {}", 
                        customer.getCustomerId(), requestedCustomerId);
                throw new AccessDeniedException("Access denied. Customers can only access their own data.");
            }
            log.debug("Customer access granted for customer: {}", requestedCustomerId);
            return;
        }
        
        log.warn("Access denied - unauthorized access attempt for customer: {}", requestedCustomerId);
        throw new AccessDeniedException("Access denied. Unauthorized access attempt.");
    }

    public boolean hasRole(String role) {
        if (authentication == null) {
            return false;
        }
        return authentication.getAuthorities().contains(new SimpleGrantedAuthority(role));
    }
} 
