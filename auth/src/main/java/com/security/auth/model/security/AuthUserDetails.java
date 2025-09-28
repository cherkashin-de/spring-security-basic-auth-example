package com.security.auth.model.security;

import com.security.auth.constant.Supportive;
import com.security.auth.model.request.UserCreate;
import com.security.auth.model.request.UserUpdate;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AuthUserDetails implements UserDetails {

    private String username;
    private String password;
    private String fullName;
    private Timestamp birthDay;
    private Double salary;

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

    public static AuthUserDetails ofResultSet(ResultSet rs) throws SQLException {
        AuthUserDetails userDetails = null;
        List<GrantedAuthority> authorities = new ArrayList<>();

        while (rs.next()) {
            if(userDetails == null)
                userDetails = AuthUserDetails.builder()
                        .username(rs.getString("login"))
                        .password(rs.getString("password"))
                        .build();

            authorities.add(new SimpleGrantedAuthority(rs.getString("role")));
        }
        if(userDetails != null)
            userDetails.setAuthorities(authorities);

        return userDetails;
    }

    public static AuthUserDetails ofUserCreate(UserCreate userCreate) {
        return AuthUserDetails.builder()
                .username(userCreate.username())
                .password(userCreate.password())
                .authorities(List.of(new SimpleGrantedAuthority(Supportive.Roles.USER)))
                .birthDay(userCreate.birthDay())
                .fullName(userCreate.fullName())
                .salary(userCreate.salary())
                .build();
    }

    public static AuthUserDetails ofUserUpdate(UserUpdate userUpdate) {
        return AuthUserDetails.builder()
                .username(userUpdate.username())
                .authorities(List.of(new SimpleGrantedAuthority(Supportive.Roles.USER)))
                .birthDay(userUpdate.birthDay())
                .fullName(userUpdate.fullName())
                .salary(userUpdate.salary())
                .build();
    }

}
