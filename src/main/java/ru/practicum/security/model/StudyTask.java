package ru.practicum.security.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyTask {

    private Long id;
    private String ownerUsername;
    private String title;
    private String description;
    private boolean completed;
    private Instant createdAt;
    private Instant completedAt;
}