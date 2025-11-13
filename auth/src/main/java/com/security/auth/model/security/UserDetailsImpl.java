package com.security.auth.model.security;

import com.security.auth.constant.Supportive;
import com.security.auth.model.entity.User;
import com.security.auth.model.request.UserCreate;
import com.security.auth.model.request.UserUpdate;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserDetailsImpl implements UserDetails {

    private Integer id;
    private String username;
    private String password;

    private Collection<? extends GrantedAuthority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

    public static UserDetailsImpl ofEntityUser(User user) {
        return UserDetailsImpl.builder()
                .id(user.getId().intValue())
                .username(user.getLogin())
                .password(user.getPassword())
                .build();
    }

    public static UserDetailsImpl ofUserCreate(UserCreate userCreate) {
        return UserDetailsImpl.builder()
                .username(userCreate.getUsername())
                .password(userCreate.getPassword())
                .authorities(List.of(new SimpleGrantedAuthority(Supportive.Roles.USER)))
                .build();
    }

    public static UserDetailsImpl ofUserUpdate(UserUpdate userUpdate) {
        return UserDetailsImpl.builder()
                .username(userUpdate.username())
                .authorities(List.of(new SimpleGrantedAuthority(Supportive.Roles.USER)))
                .build();
    }

}
