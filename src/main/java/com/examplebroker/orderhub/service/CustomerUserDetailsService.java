package com.examplebroker.orderhub.service;

import com.examplebroker.orderhub.repository.CustomerRepository;
import com.examplebroker.orderhub.security.CustomerUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerUserDetailsService implements UserDetailsService {

    private final CustomerRepository customerRepository;

    //todo: this method should be cached,
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Attempting to load user by username: {}", username);
        return customerRepository.findByUsername(username)
                .map(customer -> {
                    log.debug("Found customer: {}", customer.getUsername());
                    return new CustomerUserDetails(customer);
                })
                .orElseThrow(() -> {
                    log.warn("User not found with username: {}", username);
                    return new UsernameNotFoundException("Customer not found with username: " + username);
                });
    }
    
    public UserDetails loadUserByCustomerId(String customerId) throws UsernameNotFoundException {
        log.debug("Attempting to load user by customerId: {}", customerId);
        return customerRepository.findByCustomerId(customerId)
                .map(customer -> {
                    log.debug("Found customer by ID: {}", customer.getCustomerId());
                    return new CustomerUserDetails(customer);
                })
                .orElseThrow(() -> {
                    log.warn("User not found with customerId: {}", customerId);
                    return new UsernameNotFoundException("Customer not found with customerId: " + customerId);
                });
    }

} 
