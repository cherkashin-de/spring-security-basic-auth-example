package com.security.auth.repository;

import com.security.auth.model.entity.AuthorityUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityUserRepository extends JpaRepository<AuthorityUser, Long> {
}
