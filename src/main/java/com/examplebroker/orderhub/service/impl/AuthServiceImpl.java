package com.examplebroker.orderhub.service.impl;

import com.examplebroker.orderhub.dto.AuthenticationRequest;
import com.examplebroker.orderhub.dto.AuthenticationResponse;
import com.examplebroker.orderhub.dto.RegisterRequest;
import com.examplebroker.orderhub.model.Customer;
import com.examplebroker.orderhub.repository.CustomerRepository;
import com.examplebroker.orderhub.security.CustomerUserDetails;
import com.examplebroker.orderhub.security.JwtService;
import com.examplebroker.orderhub.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final CustomerRepository customerRepository;

    @Override
    @Transactional
    public AuthenticationResponse register(RegisterRequest request) {
        log.info("Processing registration for user: {}", request.getUsername());
        
        if (!isUsernameAvailable(request.getUsername())) {
            throw new IllegalArgumentException("Username is already taken");
        }
        
        if (!isEmailAvailable(request.getEmail())) {
            throw new IllegalArgumentException("Email is already in use");
        }
        
        if (!isCustomerIdAvailable(request.getCustomerId())) {
            throw new IllegalArgumentException("Customer ID is already in use");
        }
        
        // Create and save customer
        Customer customer = Customer.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .customerId(request.getCustomerId())
                .email(request.getEmail())
                .fullName(request.getFullName())
                .build();
        
        Customer savedCustomer = customerRepository.save(customer);
        log.info("Successfully registered user: {}", savedCustomer.getUsername());
        
        CustomerUserDetails userDetails = new CustomerUserDetails(savedCustomer);
        String token = jwtService.generateToken(userDetails);
        return buildAuthenticationResponse(savedCustomer, token);
    }

    @Override
    public AuthenticationResponse login(AuthenticationRequest request) {
        log.info("Processing login for user: {}", request.getUsername());
        
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtService.generateToken(userDetails);
            
            if (userDetails instanceof Customer) {
                return buildAuthenticationResponse((Customer) userDetails, token);
            } else {
                // Handle admin user case
                return AuthenticationResponse.builder()
                        .token(token)
                        .username(userDetails.getUsername())
                        .role(userDetails.getAuthorities().stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.joining(",")))
                        .build();
            }
        } catch (BadCredentialsException e) {
            log.warn("Invalid credentials for user: {}", request.getUsername());
            throw new BadCredentialsException("Invalid username or password");
        } catch (AuthenticationException e) {
            log.error("Authentication error for user {}: {}", request.getUsername(), e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean isUsernameAvailable(String username) {
        return !customerRepository.existsByUsername(username);
    }

    @Override
    public boolean isEmailAvailable(String email) {
        return !customerRepository.existsByEmail(email);
    }

    @Override
    public boolean isCustomerIdAvailable(String customerId) {
        return !customerRepository.existsByCustomerId(customerId);
    }

    private AuthenticationResponse buildAuthenticationResponse(Customer customer, String token) {
        return AuthenticationResponse.builder()
                .token(token)
                .username(customer.getUsername())
                .customerId(customer.getCustomerId())
                .email(customer.getEmail())
                .fullName(customer.getFullName())
                .role("ROLE_CUSTOMER")
                .build();
    }
} 
