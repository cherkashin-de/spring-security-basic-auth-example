package com.security.auth.service;

import com.security.auth.model.entity.Role;
import com.security.auth.repository.RoleRepository;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleService {

    RoleRepository roleRepository;

    public List<String> getRoles() {
        return roleRepository.getRoles();
    }

    public Integer getIdByRoleName(String role) {
        return roleRepository.getRoleIdByRoleName(role);
    }

    public Optional<Role> findByName(String role) {
        return roleRepository.findByName(role);
    }

    public void createRole(Role role) {
        roleRepository.save(role);
    }
}
