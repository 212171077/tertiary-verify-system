package com.tertiary.verify.controllers;

import com.tertiary.verify.data_model.model.beans.*;
import com.tertiary.verify.security.services.impl.UserDetailsImpl;
import com.tertiary.verify.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/tertiary_verify/user")
public class UserController {
  private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
  @Autowired private UserService userService;


  @GetMapping("/find_user_details_by_id/{id}")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<?> findUserDetailsByID(@PathVariable("id") Long id) {
    LOGGER.info("Finding user by ID: {}", id);
    return userService.findUserByID(id);
  }

  @GetMapping("/find_all_user_details")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> findAllUserDetails() {
    return userService.findAllUsers();
  }


  @GetMapping("/find_all_roles")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> findAllRoles() {
    return userService.findRoleList();
  }



  @DeleteMapping("/delete_user_by_id/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> deleteAppConfig(@PathVariable("id") Long id) {
    LOGGER.info("Deleting aUser, ID: {}", id);
    return userService.deleteUserById(id);
  }

  @PostMapping("/register_user")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> registerUser(@Valid @RequestBody PersonalDetailsRequest userDetails) {
    LOGGER.info("Registering user, User Details: {}", userDetails);
    return userService.registerUser(userDetails);
  }

  @PostMapping("/update_user_roles")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> updateUserRoles(@Valid @RequestBody UserRoleRequest request) {
    LOGGER.info("Updating user roles, request: {}", request);
    return userService.updateUserRoles(request);
  }

  @PostMapping("/update_user_details")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<?> updateUserDetails(
      @Valid @RequestBody PersonalDetailsRequest userDetails) {
    return userService.updateUser(userDetails);
  }

  @PostMapping("/update_address_details")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<?> updateAddressDetails(
      @Valid @RequestBody AddressDetailsRequest addressDetailsRequest) {
    return userService.updateAddress(addressDetailsRequest);
  }

  @PostMapping("/change_password")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePassRequest changePassRequest) {
    return userService.changePassword(changePassRequest);
  }

  @PostMapping("/update_password")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<?> updatePassword(@Valid @RequestBody UpdatePassRequest changePassRequest) {
    return userService.updatePassword(changePassRequest);
  }

  @PostMapping("/update_profile_image")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<MessageResponse> updateProfileImage(
      @Valid @RequestBody ProfileImageRequest request,
      @AuthenticationPrincipal UserDetailsImpl userPrincipal) {
    return userService.updateProfileImage(userPrincipal.getId(), request);
  }

}
