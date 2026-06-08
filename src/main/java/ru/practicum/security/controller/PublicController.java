package ru.practicum.security.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.security.dto.AppInfoResponse;
import ru.practicum.security.dto.TaskExampleResponse;
import ru.practicum.security.service.TaskService;

import java.util.List;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicController {

    private final TaskService taskService;

    @GetMapping("/info")
    public AppInfoResponse getInfo() {
        return new AppInfoResponse(
                "Демо Spring Security для Spring MVC",
                "Небольшое REST API для изучения базовой защиты HTTP-запросов через Spring Security.",
                List.of(
                        "GET /api/public/info",
                        "GET /api/public/task-examples"
                )
        );
    }

    @GetMapping("/task-examples")
    public List<TaskExampleResponse> getTaskExamples() {
        return taskService.getTaskExamples();
    }
}