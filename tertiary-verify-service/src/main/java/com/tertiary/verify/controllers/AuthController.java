package com.tertiary.verify.controllers;


import com.tertiary.verify.data_model.model.beans.*;
import com.tertiary.verify.service.AuthService;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/tertiary_verify/auth")
public class AuthController {
  @Autowired private AuthService authService;

  @PostMapping("/login")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
    return authService.authenticateUser(loginRequest);
  }

  @PostMapping("/forgot_password")
  public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
    return authService.forgotPassword(request);
  }
  
  @GetMapping("/refresh_token_expiry_time/{email}")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<?> refreshTokenExpiryTime(@PathVariable("email") String email) {
    return authService.refreshExpiredToken(email);
  }
  
}
