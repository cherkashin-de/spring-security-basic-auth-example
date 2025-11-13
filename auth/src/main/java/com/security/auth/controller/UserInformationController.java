package com.security.auth.controller;

import com.security.auth.model.request.UserInformationUpdate;
import com.security.auth.model.response.UserDetailsResponse;
import com.security.auth.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/information")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserInformationController {

    UserService userService;

    @PostMapping()
    public ResponseEntity<?> createInformation(@RequestBody UserInformationUpdate userInformationUpdate) {
        userService.updateUserInformation(userInformationUpdate);
        return ResponseEntity.ok().build();
    }

    @PutMapping()
    public ResponseEntity<?> updateUserInformation(@RequestBody UserInformationUpdate user) {
        userService.updateUserInformation(user);
        return ResponseEntity.ok().build();
    }

    @GetMapping() //Поменять на me
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        UserDetailsResponse user = userService.getUserDetailsByPrincipal(authentication);

        if(user == null)
            throw new UsernameNotFoundException("User not found");

        return ResponseEntity.ok(user);
    }

}
