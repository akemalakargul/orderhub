package com.examplebroker.orderhub.security;

import com.examplebroker.orderhub.model.Customer;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
public class CustomerUserDetails implements UserDetails {
    
    private final Customer customer;
    
    public CustomerUserDetails(Customer customer) {
        this.customer = customer;
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_CUSTOMER"));
    }
    
    @Override
    public String getPassword() {
        return customer.getPassword();
    }
    
    @Override
    public String getUsername() {
        return customer.getUsername();
    }

} 
