package com.security.auth.service;

import com.security.auth.exception.ErrorCreateUserException;
import com.security.auth.exception.ErrorDeleteUserException;
import com.security.auth.exception.ErrorUpdateUserException;
import com.security.auth.model.dto.UserWithListRole;
import com.security.auth.model.dto.UserWithRole;
import com.security.auth.model.entity.User;
import com.security.auth.model.entity.UserInformation;
import com.security.auth.model.request.UserCreate;
import com.security.auth.model.request.UserUpdate;
import com.security.auth.model.response.UserDetailsResponse;
import com.security.auth.model.security.UserDetailsImpl;
import com.security.auth.repository.UserInformationRepository;
import com.security.auth.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.security.auth.model.dto.UserWithListRole.ofUserWithRole;
import static com.security.auth.model.security.UserDetailsImpl.ofUserCreate;
import static com.security.auth.model.security.UserDetailsImpl.ofUserUpdate;
import static com.security.auth.service.utils.FieldsHelper.isFilled;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserService {

    UserRepository userRepository;
    UserDetailsManager userDetailsManager;
    UserInformationRepository userInformationRepository;

    public void createUser(UserCreate userCreate) {
        try {
            userDetailsManager.createUser(ofUserCreate(userCreate));
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ErrorCreateUserException(e.getMessage(), e);
        }
    }

    public void createUserInformation(UserCreate user) {
        userInformationRepository.save(UserInformation.builder()
                .user(userRepository.getUserByLogin(user.getUsername()).get())
                .fullName(user.getFullName())
                .birthday(user.getBirthDay())
                .salary(user.getSalary())
                .build());
    }

    public void deleteUser(String username) {
        try {
            userDetailsManager.deleteUser(username);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ErrorDeleteUserException(e.getMessage(), e);
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
        return new ArrayList<>(mapUser.values());
    }

    public void updateUser(UserUpdate userUpdate) {
        try {
            userDetailsManager.updateUser(ofUserUpdate(userUpdate));
            userRepository.getUserByLogin(userUpdate.username())
                    .ifPresent(user -> updateUserInformation(user, userUpdate));
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ErrorUpdateUserException(e.getMessage(), e);
        }
    }

    private void updateUserInformation(User user, UserUpdate userUpdate) {
        UserInformation info = userInformationRepository.findById(user.getId())
                .orElse(UserInformation.builder()
                        .user(user)
                        .fullName(isFilled(userUpdate.fullName()) ? userUpdate.fullName() : user.getLogin())
                        .birthday(userUpdate.birthDay())
                        .salary(userUpdate.salary())
                        .build());

        if (isFilled(userUpdate.fullName())) {
            info.setFullName(userUpdate.fullName());
        }
        if (userUpdate.birthDay() != null) {
            info.setBirthday(userUpdate.birthDay());
        }
        if (userUpdate.salary() != null) {
            info.setSalary(userUpdate.salary());
        }
        userInformationRepository.save(info);
    }


    public UserDetailsResponse getUserDetailsByPrincipal(Authentication authentication) {
        if (isFilled(authentication.getName()))
            return userRepository.getUsersByLogin(authentication.getName());

        return null;
    }

}
