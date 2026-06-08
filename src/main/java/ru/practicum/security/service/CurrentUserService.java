package ru.practicum.security.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.stereotype.Service;
import ru.practicum.security.dto.CurrentUserResponse;

import java.util.Comparator;
import java.util.List;

@Service
public class CurrentUserService {

    public CurrentUserResponse getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!isAuthenticatedUser(authentication)) {
            return new CurrentUserResponse("анонимный пользователь", false, List.of());
        }

        List<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .sorted(Comparator.naturalOrder())
                .toList();

        return new CurrentUserResponse(
                authentication.getName(),
                authentication.isAuthenticated(),
                authorities
        );
    }

    public String currentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!isAuthenticatedUser(authentication)) {
            throw new IllegalStateException("В SecurityContext нет аутентифицированного пользователя");
        }
        return authentication.getName();
    }

    public boolean isCurrentUserAuthenticated() {
        return isAuthenticatedUser(SecurityContextHolder.getContext().getAuthentication());
    }

    public boolean hasAuthority(String authority) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return isAuthenticatedUser(authentication) && authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority::equals);
    }

    private boolean isAuthenticatedUser(Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }
}