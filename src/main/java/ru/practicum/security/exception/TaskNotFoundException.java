package ru.practicum.security.exception;

public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(Long id) {
        super("Задача с id=%d не найдена".formatted(id));
    }
}