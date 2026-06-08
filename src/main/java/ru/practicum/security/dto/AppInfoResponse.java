package ru.practicum.security.dto;

import java.util.List;

public record AppInfoResponse(
        String name,
        String description,
        List<String> publicEndpoints
) {
}