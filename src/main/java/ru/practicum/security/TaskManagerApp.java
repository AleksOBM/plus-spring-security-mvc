package ru.practicum.security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;

@SpringBootApplication
@Theme("security-demo")
public class TaskManagerApp implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(TaskManagerApp.class, args);
    }
}