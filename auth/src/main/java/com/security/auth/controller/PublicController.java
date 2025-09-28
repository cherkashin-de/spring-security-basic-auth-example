package com.security.auth.controller;

import com.security.auth.model.request.UserCreate;
import com.security.auth.service.RoleService;
import com.security.auth.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PublicController {

    RoleService roleService;
    UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserCreate userCreate) {
        if (userService.createUser(userCreate))
            return ResponseEntity.status(HttpStatus.CREATED).build();
        else
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
    }

    @GetMapping("/roles")
    public ResponseEntity<?> getRoles() {
        try {
            return new ResponseEntity<>(roleService.getRoles(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
