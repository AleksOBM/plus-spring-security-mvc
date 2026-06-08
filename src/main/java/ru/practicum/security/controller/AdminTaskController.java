package ru.practicum.security.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.security.dto.CreateTaskExampleRequest;
import ru.practicum.security.dto.TaskExampleResponse;
import ru.practicum.security.dto.TaskResponse;
import ru.practicum.security.service.TaskService;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminTaskController {

    private final TaskService taskService;

    @GetMapping("/tasks")
    public List<TaskResponse> getAllTasks() {
        return taskService.getAllTasks();
    }

    @DeleteMapping("/tasks/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }

    @PostMapping("/task-examples")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskExampleResponse addTaskExample(@Valid @RequestBody CreateTaskExampleRequest request) {
        return taskService.addTaskExample(request);
    }
}