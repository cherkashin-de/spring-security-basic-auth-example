package com.security.auth.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleService {

    DataSource dataSource;

    public List<String> getRoles() throws SQLException {
        List<String> roles = new ArrayList<>();
        try (Connection conn = dataSource.getConnection()) {
            conn.setReadOnly(true);

            try (PreparedStatement statement = conn.prepareStatement("SELECT role FROM roles")) {
                try (ResultSet rs = statement.executeQuery()) {

                    while (rs.next())
                        roles.add(rs.getString("role"));
                }
            }
        }
        return roles;
    }
}
