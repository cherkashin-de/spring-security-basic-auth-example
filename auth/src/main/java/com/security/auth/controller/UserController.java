package com.security.auth.controller;

import com.security.auth.model.request.UserUpdate;
import com.security.auth.model.response.UserDetailsResponse;
import com.security.auth.service.UserDetailsResponseService;
import com.security.auth.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {

    UserDetailsResponseService userDetailsResponseService;
    UserService userService;

    @PostMapping("/update")
    public ResponseEntity<?> updateUser(@RequestBody UserUpdate user) {
        userService.updateUser(user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/information/update")
    public ResponseEntity<?> updateUserInformation(@RequestBody UserUpdate user) {
        userService.updateUser(user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        UserDetailsResponse user = userDetailsResponseService
                .getUserDetailsByPrincipal(authentication);

        if(user == null)
            throw new UsernameNotFoundException("User not found");

        return ResponseEntity.ok(user);
    }

    @GetMapping()
    public ResponseEntity<?> getUsers() {
        return ResponseEntity.ok(userService.getAllUsersWithRole());
    }

}
