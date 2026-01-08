package com.dev.quikkkk.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class EmailDomainValidator implements ConstraintValidator<NonDisposableEmail, String> {
    private final Set<String> blockedDomains;

    public EmailDomainValidator(@Value("${app.security.disposable-email}") List<String> domains) {
        this.blockedDomains = domains.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {
        if (email == null || email.isBlank()) return true;
        if (!email.contains("@")) return true;

        String domain = extractDomain(email);
        if (domain == null) return true;

        return !isBlacklisted(domain);
    }

    private String extractDomain(String email) {
        int atIndex = email.lastIndexOf("@");
        if (atIndex == -1 || atIndex == email.length() - 1) return null;
        return email.substring(atIndex + 1).toLowerCase();
    }

    private boolean isBlacklisted(String domain) {
        if (blockedDomains.contains(domain)) return true;
        int dotIndex = domain.lastIndexOf(".");

        while (dotIndex != -1) {
            domain = domain.substring(dotIndex + 1);
            if (blockedDomains.contains(domain)) return true;
            dotIndex = domain.lastIndexOf(".");
        }

        return false;
    }
}
