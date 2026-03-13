package com.dev.quikkkk.core.config;

import com.dev.quikkkk.modules.user.entity.Role;
import com.dev.quikkkk.modules.user.repository.IRoleRepository;
import com.dev.quikkkk.core.security.ApplicationAuditorAware;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

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
            createRoleIfNotExists(repository, "ADMIN");
            createRoleIfNotExists(repository, "TRAINER");
            createRoleIfNotExists(repository, "CLIENT");
        };
    }

    @Bean
    public AuditorAware<String> auditorAware() {
        return new ApplicationAuditorAware();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) {
        return config.getAuthenticationManager();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
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
