package ru.practicum.security.repository;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;
import ru.practicum.security.model.TaskExample;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryTaskExampleRepository {

    private final ConcurrentMap<Long, TaskExample> examples = new ConcurrentHashMap<>();
    private final AtomicLong idSequence = new AtomicLong();

    @PostConstruct
    void init() {
        saveNew("Попробуйте публичный эндпоинт", "Выполните запрос GET /api/public/info без учётных данных.");
        saveNew("Попробуйте пользовательский эндпоинт", "Выполните запрос GET /api/tasks/my с учётными данными ivan/ivan.");
        saveNew("Попробуйте административный эндпоинт", "Выполните запрос GET /api/admin/tasks с учётными данными anna/anna.");
    }

    public TaskExample saveNew(String title, String description) {
        long id = idSequence.incrementAndGet();
        TaskExample example = TaskExample.builder()
                .id(id)
                .title(title)
                .description(description)
                .build();
        examples.put(id, example);
        return example;
    }

    public List<TaskExample> findAll() {
        return examples.values().stream()
                .sorted(Comparator.comparing(TaskExample::getId))
                .toList();
    }
}