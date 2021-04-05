package com.mogaco.project.auth.application;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationGuard {

    public boolean checkIdMatch(Authentication authentication, Long id) {
        if (authentication == null) {
            return false;
        }
        Long userDetailId = (Long) authentication.getPrincipal();
        return id.equals(userDetailId);
    }
}
