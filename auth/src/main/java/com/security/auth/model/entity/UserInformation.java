package com.security.auth.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInformation implements Serializable {

    @Id
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private User user;

    @Column(name = "fullName", nullable = false)
    private String fullName;

    @Column(name = "birthday")
    private Timestamp birthday;

    @Column(name = "salary")
    private Double salary;

}
