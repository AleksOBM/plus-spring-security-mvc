package ru.practicum.security.dto;

import java.util.List;

public record CurrentUserResponse(
        String username,
        boolean authenticated,
        List<String> authorities
) {
}