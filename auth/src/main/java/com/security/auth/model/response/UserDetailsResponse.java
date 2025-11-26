package com.security.auth.model.response;

import lombok.*;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class UserDetailsResponse implements Serializable {

    @EqualsAndHashCode.Exclude
    private String userName;

    private String fullName;
    private Timestamp birthDay;
    private Double salary;
    private String roles;

    public UserDetailsResponse(String userName, String fullName, Timestamp birthDay, Double salary) {
        this.userName = userName;
        this.fullName = fullName;
        this.birthDay = birthDay;
        this.salary = salary;
    }

}
