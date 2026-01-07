package com.dev.quikkkk.config;

import com.dev.quikkkk.entity.Role;
import com.dev.quikkkk.repository.IRoleRepository;
import com.dev.quikkkk.security.ApplicationAuditorAware;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Configuration
@Slf4j
public class BeansConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CommandLineRunner commandLineRunner(IRoleRepository repository) {
        return args -> {
            createRoleIfNotExists(repository, "ROLE_ADMIN");
            createRoleIfNotExists(repository, "ROLE_TRAINER");
            createRoleIfNotExists(repository, "ROLE_CLIENT");
        };
    }

    @Bean
    public AuditorAware<String> auditorAware() {
        return new ApplicationAuditorAware();
    }

    private void createRoleIfNotExists(IRoleRepository repository, String roleName) {
        Optional<Role> existingRole = repository.findByName(roleName);
        log.info("Checking if role {} exists", roleName);

        if (existingRole.isEmpty()) {
            Role role = Role.builder()
                    .name(roleName)
                    .createdBy("system")
                    .build();

            repository.save(role);
            log.info("Role {} created", roleName);
        }
    }
}
