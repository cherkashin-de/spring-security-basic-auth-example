package com.security.auth.service;

import com.security.auth.model.response.UserDetailsResponse;
import com.security.auth.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import static com.security.auth.service.utils.FieldsHelper.isFilled;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserDetailsResponseService {

    UserRepository userRepository;

    public UserDetailsResponse getUserDetailsByPrincipal(Authentication authentication) {
        if(isFilled(authentication.getName()))
            return userRepository.getUsersByLogin(authentication.getName());

        return null;
    }

}
