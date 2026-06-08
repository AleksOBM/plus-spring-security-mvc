package ru.practicum.security.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.security.dto.CreateTaskExampleRequest;
import ru.practicum.security.dto.CreateTaskRequest;
import ru.practicum.security.dto.TaskExampleResponse;
import ru.practicum.security.dto.TaskResponse;
import ru.practicum.security.exception.TaskNotFoundException;
import ru.practicum.security.model.StudyTask;
import ru.practicum.security.model.TaskExample;
import ru.practicum.security.repository.InMemoryTaskExampleRepository;
import ru.practicum.security.repository.InMemoryTaskRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final InMemoryTaskRepository taskRepository;
    private final InMemoryTaskExampleRepository taskExampleRepository;
    private final CurrentUserService currentUserService;

    public List<TaskExampleResponse> getTaskExamples() {
        return taskExampleRepository.findAll().stream()
                .map(this::toTaskExampleResponse)
                .toList();
    }

    public TaskExampleResponse addTaskExample(CreateTaskExampleRequest request) {
        TaskExample example = taskExampleRepository.saveNew(request.title(), request.description());
        return toTaskExampleResponse(example);
    }

    public List<TaskResponse> getMyTasks() {
        String username = currentUserService.currentUsername();
        return taskRepository.findByOwnerUsername(username).stream()
                .map(this::toTaskResponse)
                .toList();
    }

    public TaskResponse createTask(CreateTaskRequest request) {
        String username = currentUserService.currentUsername();
        StudyTask task = taskRepository.saveNew(username, request.title(), request.description());
        return toTaskResponse(task);
    }

    public TaskResponse completeMyTask(Long id) {
        String username = currentUserService.currentUsername();
        StudyTask task = taskRepository.completeByIdAndOwner(id, username)
                .orElseThrow(() -> new TaskNotFoundException(id));
        return toTaskResponse(task);
    }

    public List<TaskResponse> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(this::toTaskResponse)
                .toList();
    }

    public void deleteTask(Long id) {
        boolean deleted = taskRepository.deleteById(id);
        if (!deleted) {
            throw new TaskNotFoundException(id);
        }
    }

    private TaskResponse toTaskResponse(StudyTask task) {
        return new TaskResponse(
                task.getId(),
                task.getOwnerUsername(),
                task.getTitle(),
                task.getDescription(),
                task.isCompleted(),
                task.getCreatedAt(),
                task.getCompletedAt()
        );
    }

    private TaskExampleResponse toTaskExampleResponse(TaskExample example) {
        return new TaskExampleResponse(
                example.getId(),
                example.getTitle(),
                example.getDescription()
        );
    }
}