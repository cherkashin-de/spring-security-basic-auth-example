package com.security.auth.service.security;

import com.security.auth.constant.Supportive;
import com.security.auth.model.security.PrincipalImpl;
import com.security.auth.model.security.UserDetailsImpl;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AuthenticationProviderImpl implements AuthenticationProvider {

    UserDetailsService authUserDetailsService;
    PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UserDetails userDetails = authUserDetailsService.loadUserByUsername(authentication.getName());
        if (userDetails == null)
            return null;

        if (passwordEncoder.matches(authentication.getCredentials().toString(), userDetails.getPassword())) {
            UsernamePasswordAuthenticationToken result = UsernamePasswordAuthenticationToken
                    .authenticated(new PrincipalImpl(userDetails.getUsername()),
                            authentication.getCredentials(), userDetails.getAuthorities());

            result.setDetails(authentication.getDetails());
            return result;
        }

        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }

}
