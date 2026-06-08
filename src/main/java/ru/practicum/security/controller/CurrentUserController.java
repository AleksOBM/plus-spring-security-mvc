package ru.practicum.security.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.security.dto.CurrentUserResponse;
import ru.practicum.security.service.CurrentUserService;

@RestController
@RequiredArgsConstructor
public class CurrentUserController {

    private final CurrentUserService currentUserService;

    @GetMapping("/api/me")
    public CurrentUserResponse getCurrentUser() {
        return currentUserService.getCurrentUser();
    }
}