package com.examplebroker.orderhub.service;

import com.examplebroker.orderhub.dto.AuthenticationRequest;
import com.examplebroker.orderhub.dto.AuthenticationResponse;
import com.examplebroker.orderhub.dto.RegisterRequest;

public interface AuthService {

    AuthenticationResponse register(RegisterRequest request);

    AuthenticationResponse login(AuthenticationRequest request);

    boolean isUsernameAvailable(String username);

    boolean isEmailAvailable(String email);

    boolean isCustomerIdAvailable(String customerId);
}
