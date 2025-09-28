package com.security.auth.service.security;

import com.security.auth.service.UserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsManagerImpl implements UserDetailsManager {

    private final UserDetailsService userDetailsService;

    @Override
    public void createUser(UserDetails user) {
        userDetailsService.createUser(user);
    }

    @Override
    public void updateUser(UserDetails user) {
        boolean result = userDetailsService.updateUser(user);
        if (!result)
            log.info("UserDetailsManager %s update failed".formatted(user));
    }

    @Override
    public void deleteUser(String username) {

    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {

    }

    @Override
    public boolean userExists(String username) {
        return userDetailsService.isUserExists(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails user = userDetailsService.getUserDetails(username);
        if (user == null)
            throw new UsernameNotFoundException(username);

        return user;
    }


}