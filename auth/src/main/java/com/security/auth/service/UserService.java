package com.security.auth.service;

import com.security.auth.exception.ErrorCreateUserException;
import com.security.auth.exception.ErrorDeleteUserException;
import com.security.auth.exception.ErrorUpdateUserException;
import com.security.auth.model.dto.UserWithListRole;
import com.security.auth.model.dto.UserWithRole;
import com.security.auth.model.request.UserCreate;
import com.security.auth.model.request.UserInformationUpdate;
import com.security.auth.model.request.UserUpdate;
import com.security.auth.model.response.UserDetailsResponse;
import com.security.auth.model.security.UserDetailsImpl;
import com.security.auth.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.security.auth.constant.Supportive.SQLQuery.SQL_GET_ID_USER;
import static com.security.auth.constant.Supportive.SQLQuery.SQL_INSERT_USER_INFORMATION_BY_USER_ID;
import static com.security.auth.model.dto.UserWithListRole.ofUserWithRole;
import static com.security.auth.model.security.UserDetailsImpl.ofUserCreate;
import static com.security.auth.model.security.UserDetailsImpl.ofUserUpdate;
import static com.security.auth.service.utils.FieldsHelper.isFilled;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserService {

    DataSource dataSource;
    UserRepository userRepository;
    UserDetailsManager userDetailsManager;

    public void createUser(UserCreate userCreate) {
        try {
            UserDetailsImpl user = ofUserCreate(userCreate);
            userDetailsManager.createUser(user);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ErrorCreateUserException(e.getMessage(), e);
        }
    }

    public void createUserInformation(UserCreate user) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement statement = conn.prepareStatement(SQL_INSERT_USER_INFORMATION_BY_USER_ID)) {

            fillStatementForInsertUserInformation(statement, user);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteUser(String username) {
        try {
            userDetailsManager.deleteUser(username);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ErrorDeleteUserException(e.getMessage(), e);
        }
    }

    //todo: Rewrite
    public boolean updateUserInformation(UserInformationUpdate user) {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            conn.setReadOnly(true);

            Integer userId = null;
            try (PreparedStatement statement = conn.prepareStatement(SQL_GET_ID_USER)) {
                statement.setString(1, user.fullName());

                ResultSet rs = statement.executeQuery();
                while (rs.next()) {
                    userId = rs.getInt(1);
                }
            }
            conn.setReadOnly(false);

            if (userId == null)
                throw new UsernameNotFoundException("User not found");

            try (PreparedStatement statement = conn.prepareStatement(SQL_INSERT_USER_INFORMATION_BY_USER_ID)) {
//                fillStatementForInsertUserInformation(statement, userId, user);
                statement.executeUpdate();
            }

            return true;
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    public List<UserWithListRole> getAllUsersWithRole() {
        List<UserWithRole> users = userRepository.getAllUsersWithRole();
        if (users.isEmpty())
            return List.of();

        Map<String, UserWithListRole> mapUser = new HashMap<>();

        for (UserWithRole userWithRole : users) {
            UserWithListRole user = mapUser.get(userWithRole.login());
            if (user == null) {
                mapUser.put(userWithRole.login(), ofUserWithRole(userWithRole));
            } else {
                user.getRoles().add(userWithRole.role());
                mapUser.put(userWithRole.login(), user);
            }
        }
        return new ArrayList<>(mapUser.values());
    }

    public void updateUser(UserUpdate userUpdate) {
        try {
            userDetailsManager.updateUser(ofUserUpdate(userUpdate));
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ErrorUpdateUserException(e.getMessage(), e);
        }
    }


    public UserDetailsResponse getUserDetailsByPrincipal(Authentication authentication) {
        if (isFilled(authentication.getName()))
            return userRepository.getUsersByLogin(authentication.getName());

        return null;
    }

    private void fillStatementForInsertUserInformation(PreparedStatement statement, UserCreate user) throws SQLException {
        statement.setInt(1, user.getId());

        if (user.getBirthDay() != null) statement.setTimestamp(2, user.getBirthDay());
        else statement.setNull(2, Types.DATE);

        if (user.getFullName() != null) statement.setString(3, user.getFullName());
        else statement.setNull(3, Types.VARCHAR);

        if (user.getSalary() != null) statement.setDouble(4, user.getSalary());
        else statement.setNull(4, Types.DOUBLE);
    }

}
