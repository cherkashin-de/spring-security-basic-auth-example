package com.security.auth.service;

import com.security.auth.model.security.AuthUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;

import static com.security.auth.constant.Supportive.SQLQuery.*;
import static com.security.auth.model.security.AuthUserDetails.ofResultSet;
import static com.security.auth.service.utils.FieldsHelper.isFilled;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserDetailsService {

    private final DataSource dataSource;
    private final PasswordEncoder passwordEncoder;

    public boolean createUser(UserDetails authUserDetails) {
        if (authUserDetails instanceof AuthUserDetails user)
            try (Connection conn = dataSource.getConnection()) {
                conn.setAutoCommit(false);

                try (PreparedStatement ps = conn.prepareStatement(SQL_INSERT_USERS)) {
                    ps.setString(1, user.getUsername());
                    ps.setString(2, passwordEncoder.encode(user.getPassword()));

                    ps.executeUpdate();
                }

                conn.setReadOnly(true);
                Integer userID = null;
                if (isFilled(user.getAuthorities()))
                    try (PreparedStatement ps = conn.prepareStatement(SQL_GET_ID_USER)) {

                        ps.setString(1, user.getUsername());
                        try (ResultSet rs = ps.executeQuery()) {
                            while (rs.next())
                                userID = rs.getInt(1);
                        }
                    }

                conn.setReadOnly(false);

                if (userID != null) {
                    for (GrantedAuthority grantedAuthority : user.getAuthorities())
                        try (PreparedStatement ps = conn.prepareStatement(SQL_INSERT_AUTHORITY_BY_USER_ID)) {
                            Integer roleId = getIdByRole(grantedAuthority.getAuthority(), conn);
                            if (roleId != null) {
                                ps.setInt(1, userID);
                                ps.setInt(2, roleId);

                                ps.executeUpdate();
                            }
                        }

                    try (PreparedStatement statement = conn.prepareStatement(SQL_INSERT_USER_INFORMATION_BY_USER_ID)) {
                        fillStatementForInsertUserInformation(statement, userID, user);
                        statement.executeUpdate();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }

                    conn.commit();
                } else conn.rollback();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        return true;
    }

    public boolean updateUser(UserDetails user) {
        if (user instanceof AuthUserDetails authUserDetails) {
            try (Connection conn = dataSource.getConnection()) {
                conn.setAutoCommit(false);
                conn.setReadOnly(true);

                Integer userId = null;
                try (PreparedStatement statement = conn.prepareStatement(SQL_GET_ID_USER)) {
                    statement.setString(1, authUserDetails.getUsername());

                    ResultSet rs = statement.executeQuery();
                    while (rs.next()) {
                        userId = rs.getInt(1);
                    }
                }
                conn.setReadOnly(false);

                if (userId == null)
                    throw new UsernameNotFoundException("User not found");

                try (PreparedStatement statement = conn.prepareStatement(SQL_INSERT_USER_INFORMATION_BY_USER_ID)) {
                    fillStatementForInsertUserInformation(statement, userId, authUserDetails);
                    statement.executeUpdate();
                }

                return true;
            } catch (SQLException e) {
                log.error(e.getMessage(), e);
                return false;
            }
        }
        return false;
    }

    public boolean isUserExists(final String username) {
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(SQL_EXISTS_USER_BY_ID)) {
                ps.setString(1, username);

                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public UserDetails getUserDetails(final String login) {
        UserDetails userDetails = null;
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(SQL_GET_USERS)) {
                ps.setString(1, login);

                try (ResultSet rs = ps.executeQuery();) {
                    userDetails = ofResultSet(rs);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return userDetails;
    }

    private Integer getIdByRole(String role, Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_GET_ROLE_BY_NAME)) {
            ps.setString(1, role);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return null;
    }

    private void fillStatementForInsertUserInformation(PreparedStatement statement, Integer userId, AuthUserDetails user) throws SQLException {
        statement.setInt(1, userId);

        if (user.getBirthDay() != null)
            statement.setTimestamp(2, user.getBirthDay());
        else
            statement.setNull(2, Types.DATE);

        if (user.getFullName() != null)
            statement.setString(3, user.getFullName());
        else
            statement.setNull(3, Types.VARCHAR);

        if (user.getSalary() != null)
            statement.setDouble(4, user.getSalary());
        else
            statement.setNull(4, Types.DOUBLE);
    }

}
