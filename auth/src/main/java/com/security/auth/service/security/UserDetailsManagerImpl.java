package com.security.auth.service.security;

import com.security.auth.model.entity.AuthorityUser;
import com.security.auth.model.entity.Role;
import com.security.auth.model.entity.User;
import com.security.auth.model.security.UserDetailsImpl;
import com.security.auth.repository.AuthorityUserRepository;
import com.security.auth.repository.RoleRepository;
import com.security.auth.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserDetailsManagerImpl implements UserDetailsManager {

    RoleRepository roleRepository;
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    AuthorityUserRepository authorityUserRepository;

    @Override
    public void createUser(UserDetails user) {
        var newUser = userRepository.save(User.builder()
                .login(user.getUsername())
                .password(passwordEncoder.encode(user.getPassword()))
                .build());

        user.getAuthorities().forEach(authority -> {
            Role role = roleRepository.findByName(authority.getAuthority()).orElse(null);
            if (role == null)
                role = roleRepository.save(Role.builder().name(authority.getAuthority()).build());

            authorityUserRepository.save(AuthorityUser.builder()
                    .user(newUser)
                    .role(role)
                    .build());
        });
    }

    @Override
    public void updateUser(UserDetails user) {
        var findUserOptional = userRepository.findByLogin(user.getUsername());
        Assert.isTrue(findUserOptional.isPresent(), "User not found");

        var findUser = findUserOptional.get();
        findUserOptional.get().setLogin(user.getUsername());

        userRepository.save(findUser);
    }

    @Override
    public void deleteUser(String username) {
        userRepository.deleteByLogin(username);
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();

        Assert.notNull(authentication, "Authentication object must not be null");

        String username = authentication.getName();
    }

    @Override
    public boolean userExists(String username) {
        return userRepository.existsByLogin(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails user = getUserDetails(username);
        if (user == null)
            throw new UsernameNotFoundException(username);

        return user;
    }

    private UserDetails getUserDetails(final String login) {
        Optional<User> userOptional = userRepository.findByLogin(login);
        return userOptional.map(UserDetailsImpl::ofEntityUser).orElse(null);
    }

}