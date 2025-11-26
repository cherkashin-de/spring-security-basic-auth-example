package com.security.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.security.auth.constant.Supportive;
import com.security.auth.model.entity.AuthorityUser;
import com.security.auth.model.entity.Role;
import com.security.auth.model.entity.User;
import com.security.auth.repository.AuthorityUserRepository;
import com.security.auth.repository.RoleRepository;
import com.security.auth.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public CommandLineRunner commandLineRunner(UserRepository userRepository,
                                               AuthorityUserRepository authorityUserRepository,
                                               RoleRepository roleRepository,
                                               PasswordEncoder passwordEncoder) throws InterruptedException {

        if (userRepository.existsByLogin("admin"))
            return args -> {
            };

        try {
            User user = userRepository.save(User.builder()
                    .login("admin")
                    .password(passwordEncoder.encode("1111"))
                    .build());

            Role role = roleRepository.save(Role.builder()
                    .name(Supportive.Roles.ADMIN).build());

            authorityUserRepository.save(AuthorityUser.builder()
                    .user(user)
                    .role(role)
                    .build());

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        return args -> {
        };
    }

}
