package com.security.auth.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserWithListRole {

    private String login;
    private String fullName;
    private Timestamp birthDay;
    private Double salary;
    private Set<String> roles;

    public static UserWithListRole ofUserWithRole(UserWithRole user) {
        Set<String> roles = new HashSet<>();
        roles.add(user.role());

        return UserWithListRole.builder()
                .login(user.login())
                .fullName(user.fullName())
                .salary(user.salary())
                .birthDay(user.birthDay())
                .roles(roles)
                .build();
    }

}
