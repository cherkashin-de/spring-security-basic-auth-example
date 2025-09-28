package com.security.auth.service;

import com.security.auth.model.dto.UserWithListRole;
import com.security.auth.model.dto.UserWithRole;
import com.security.auth.model.request.UserCreate;
import com.security.auth.model.request.UserUpdate;
import com.security.auth.model.security.AuthUserDetails;
import com.security.auth.repository.UserRepository;
import com.security.auth.service.security.UserDetailsManagerImpl;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.security.auth.model.dto.UserWithListRole.ofUserWithRole;
import static com.security.auth.model.security.AuthUserDetails.ofUserCreate;
import static com.security.auth.model.security.AuthUserDetails.ofUserUpdate;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserService {

    UserRepository userRepository;
    UserDetailsService userDetailsService;

    public boolean createUser(UserCreate userCreate) {
        try {
            AuthUserDetails authUserDetails = ofUserCreate(userCreate);
            userDetailsService.createUser(authUserDetails);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    public List<UserWithListRole> getAllUsersWithRole() {
        List<UserWithRole> users = userRepository.getAllUsersWithRole();
        if (users.isEmpty())
            return List.of();

        Map<String, UserWithListRole> mapUser = new HashMap<>();

        for (UserWithRole userWithRole : users) {
            UserWithListRole user = mapUser.get(userWithRole.login());
            if (user == null) {
                mapUser.put(userWithRole.login(), ofUserWithRole(userWithRole));
            } else {
                user.getRoles().add(userWithRole.role());
                mapUser.put(userWithRole.login(), user);
            }
        }
        return mapUser.values().stream().collect(Collectors.toList());
    }

    public boolean updateUser(UserUpdate userUpdate) {
        try {
            AuthUserDetails authUserDetails = ofUserUpdate(userUpdate);
            userDetailsService.updateUser(authUserDetails);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

}
