package com.security.auth.model.request;

import java.io.Serializable;
import java.sql.Timestamp;

public record UserCreate(String username, String password, String fullName,
                         Timestamp birthDay,
                         Double salary) implements Serializable {
}