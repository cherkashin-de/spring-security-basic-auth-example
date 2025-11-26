package com.security.auth.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCreate implements Serializable {

    private Integer id;

    private String username;

    private String password;

    private String fullName;
    private Timestamp birthDay;
    private Double salary;
}