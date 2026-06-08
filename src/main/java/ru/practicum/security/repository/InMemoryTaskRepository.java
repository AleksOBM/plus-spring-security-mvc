package ru.practicum.security.repository;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;
import ru.practicum.security.model.StudyTask;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryTaskRepository {

    private final ConcurrentMap<Long, StudyTask> tasks = new ConcurrentHashMap<>();
    private final AtomicLong idSequence = new AtomicLong();

    @PostConstruct
    void init() {
        saveNew("ivan", "Изучить SecurityFilterChain",
                "Найти место, где описаны правила защиты HTTP-запросов.");
        saveNew("ivan", "Вызвать /api/me",
                "Выполнить запрос с Basic Authentication и посмотреть список полномочий.");
        saveNew("anna", "Проверить учебные задачи",
                "Открыть административный эндпоинт и сравнить правила доступа по ролям.");
    }

    public StudyTask saveNew(String ownerUsername, String title, String description) {
        long id = idSequence.incrementAndGet();
        StudyTask task = StudyTask.builder()
                .id(id)
                .ownerUsername(ownerUsername)
                .title(title)
                .description(description)
                .completed(false)
                .createdAt(Instant.now())
                .build();
        tasks.put(id, task);
        return task;
    }

    public Optional<StudyTask> completeByIdAndOwner(Long id, String ownerUsername) {
        StudyTask task = tasks.computeIfPresent(id, (key, foundTask) -> {
            if (!foundTask.getOwnerUsername().equals(ownerUsername)) {
                return foundTask;
            }

            foundTask.setCompleted(true);
            foundTask.setCompletedAt(Instant.now());
            return foundTask;
        });

        return Optional.ofNullable(task)
                .filter(foundTask -> foundTask.getOwnerUsername().equals(ownerUsername));
    }

    public List<StudyTask> findByOwnerUsername(String username) {
        return tasks.values().stream()
                .filter(task -> task.getOwnerUsername().equals(username))
                .sorted(Comparator.comparing(StudyTask::getId))
                .toList();
    }

    public List<StudyTask> findAll() {
        return tasks.values().stream()
                .sorted(Comparator.comparing(StudyTask::getId))
                .toList();
    }

    public boolean deleteById(Long id) {
        return tasks.remove(id) != null;
    }
}