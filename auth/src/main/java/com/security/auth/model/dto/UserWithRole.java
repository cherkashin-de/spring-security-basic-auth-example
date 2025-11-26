package com.security.auth.model.dto;

import java.sql.Timestamp;

public record UserWithRole(String login, String fullName,
                           Timestamp birthDay, Double salary, String role) {
}
