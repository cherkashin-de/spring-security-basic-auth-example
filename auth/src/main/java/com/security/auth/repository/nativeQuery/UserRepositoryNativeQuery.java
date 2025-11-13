package com.security.auth.repository.nativeQuery;

import com.security.auth.model.dto.UserWithRole;
import com.security.auth.model.response.UserDetailsResponse;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UserRepositoryNativeQuery {

    @Transactional(readOnly = true)
    @Query(value = """
           SELECT
            t1.login as userName,
            t2.full_name as fullName,
            t2.birthday as birthDay,
            t2.salary as salary,
            t4.role as roles
            FROM users t1
            JOIN user_information t2
                ON t1.id = t2.user_id
            JOIN authority_user t3
                ON t1.id = t3.user_id
            JOIN roles t4
                ON t3.role_id = t4.id
                where login = :login
    """,
            nativeQuery = true)
    UserDetailsResponse getUsersByLogin(@Param("login") String login);

    @Transactional(readOnly = true)
    @Query(value = """
            SELECT
                t1.login AS login,
                t2.full_name AS fullName,
                t2.birthday AS birthDay,
                t2.salary AS salary,
                t4.role AS role
            FROM users t1
                     JOIN user_information t2
                          ON t1.id = t2.user_id
                     JOIN authority_user t3
                          ON t1.id = t3.user_id
                    JOIN roles t4
                        ON t3.role_id = t4.id
            """, nativeQuery = true)
    List<UserWithRole> getAllUsersWithRole();


}
