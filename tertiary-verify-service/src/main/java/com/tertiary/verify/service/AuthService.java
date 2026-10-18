package com.tertiary.verify.service;

import com.tertiary.verify.data_model.exeption.UserNotException;
import com.tertiary.verify.data_model.model.beans.*;
import com.tertiary.verify.data_model.model.entities.Role;
import com.tertiary.verify.data_model.model.entities.User;
import com.tertiary.verify.security.jwt.JwtUtils;
import com.tertiary.verify.security.services.impl.UserDetailsImpl;
import com.tertiary.verify.utils.ConstantUtil;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);
    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private UserService userService;
    @Autowired private JwtUtils jwtUtils;

    public ResponseEntity<JwtResponse> authenticateUser(LoginRequest loginRequest) {
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles =
                userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList());

        userService.recordLogin(userDetails.getId());

        return ResponseEntity.ok(
                new JwtResponse(
                        jwt,
                        userDetails.getId(),
                        userDetails.getUsername(),
                        userDetails.getEmail(),
                        roles,
                        userDetails.isChangePassword()));
    }

    public ResponseEntity<?> forgotPassword(ForgotPasswordRequest request) {
        try {
            String mssg =
                    "Your password has been successfully reset. "
                            + "An email has been sent to the registered email address "
                            + "associated with your account containing the new login details. "
                            + "Please check your email inbox.";
            userService.notifyUserNewPasswordEmail(request.getEmail(), request.getIdNumber());
            return ResponseEntity.ok(ResponseEntity.ok(new MessageResponse(true, mssg, null)));
        } catch (UserNotException e) {
            LOGGER.error("Error: ", e);
            return ResponseEntity.badRequest().body(new MessageResponse(false, e.getMessage(), null));
        } catch (Exception e) {
            LOGGER.error("Error: ", e);
            return ResponseEntity.ok(
                    new MessageResponse(false, ConstantUtil.SERVICE_UNAVAILABLE_MSSG, null));
        }
    }


    public ResponseEntity<?> refreshExpiredToken(String email) {
        try {

            LOGGER.info("Refresh token expiry time, Email: {}", email);

            User user = userService.findByEmailAndDeleted(email, false);

            if (user == null) {
                throw new Exception("User not found");
            }

            Set<String> roles = new HashSet<>();
            for (Role role : user.getRoles()) {
                roles.add(role.getName().name());
            }

            return ResponseEntity.ok(
                    new JwtResponse(
                            jwtUtils.refreshJwtExpiryTime(user),
                            user.getId(),
                            user.getUsername(),
                            user.getEmail(),
                            new ArrayList<>(roles),
                            user.getChangePassword()));

        } catch (Exception e) {
            LOGGER.error("Error: ", e);
            return ResponseEntity.badRequest().body(new MessageResponse(false, e.getMessage(), null));
        }
    }
    

}
