package com.security.auth.repository.nativeQuery;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepositoryNativeQuery {

    @Query(value = """
            SELECT 
                role
            FROM roles 
            """, nativeQuery = true)
    List<String> getRoles();

    @Query(value = """
            SELECT 
                id
            FROM roles
            """, nativeQuery = true)
    Integer getRoleIdByRoleName(String role);

}
