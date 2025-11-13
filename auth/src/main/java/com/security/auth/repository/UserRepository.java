package com.security.auth.repository;

import com.security.auth.model.entity.User;
import com.security.auth.repository.nativeQuery.UserRepositoryNativeQuery;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryNativeQuery {

    Optional<User> findByLogin(String username);

    boolean existsByLogin(String username);

    @Modifying
    @Transactional
    void deleteByLogin(String login);

}
