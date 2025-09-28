package com.security.auth.repository;

import com.security.auth.model.entity.Users;
import com.security.auth.repository.nativeQuery.UserRepositoryNativeQuery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<Users, Long>, UserRepositoryNativeQuery {

}
