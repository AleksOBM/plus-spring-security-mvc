package ru.practicum.security.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import ru.practicum.security.dto.CreateTaskRequest;
import ru.practicum.security.dto.TaskResponse;
import ru.practicum.security.service.CurrentUserService;
import ru.practicum.security.service.TaskService;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Route(value = "tasks", layout = MainLayout.class)
@PageTitle("Мои задачи")
public class TasksView extends VerticalLayout {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM HH:mm")
            .withZone(ZoneId.systemDefault());

    private final TaskService taskService;
    private final Grid<TaskResponse> grid = new Grid<>();
    private final TextField title = new TextField("Название");
    private final TextArea description = new TextArea("Описание");
    private final HorizontalLayout stats = UiComponents.responsiveRow();

    public TasksView(TaskService taskService, CurrentUserService currentUserService) {
        this.taskService = taskService;

        VerticalLayout page = UiComponents.page();
        page.add(
                UiComponents.pageHeader(
                        "Рабочее пространство",
                        "Мои учебные задачи",
                        "Вы вошли как " + currentUserService.currentUsername()
                                + ". Здесь можно создать задачу и отметить её выполненной."
                ),
                stats,
                createForm(),
                createGrid()
        );

        setPadding(false);
        setSpacing(false);
        add(page);
        refresh();
    }

    private Div createForm() {
        title.setPlaceholder("Например, изучить SecurityFilterChain");
        title.setRequiredIndicatorVisible(true);
        title.setWidth("320px");
        title.addValueChangeListener(event -> clearTitleError());

        description.setPlaceholder("Краткое описание задачи");
        description.setWidth("420px");

        Button create = new Button("Добавить задачу", VaadinIcon.PLUS.create(), event -> createTask());
        create.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout form = new HorizontalLayout(title, description, create);
        form.addClassName("form-row");
        form.setAlignItems(Alignment.END);

        Div card = UiComponents.card(
                new H3("Новая задача"),
                new Paragraph("Добавьте короткое название и описание, чтобы задача появилась в списке ниже."),
                form
        );
        card.addClassName("form-card");
        return card;
    }

    private Div createGrid() {
        grid.addClassName("tasks-grid");
        grid.addColumn(TaskResponse::id).setHeader("ID").setWidth("80px").setFlexGrow(0);
        grid.addColumn(TaskResponse::title).setHeader("Название").setFlexGrow(1);
        grid.addColumn(task -> normalizeDescription(task.description())).setHeader("Описание").setFlexGrow(2);
        grid.addColumn(task -> DATE_FORMATTER.format(task.createdAt()))
                .setHeader("Создана")
                .setWidth("130px")
                .setFlexGrow(0);
        grid.addComponentColumn(task -> task.completed()
                ? UiComponents.badge("Выполнена", "green")
                : UiComponents.badge("В работе", "amber")
        ).setHeader("Статус").setWidth("140px").setFlexGrow(0);
        grid.addComponentColumn(task -> {
            Button complete = new Button("Выполнить", VaadinIcon.CHECK.create(), event -> completeTask(task.id()));
            complete.addThemeVariants(ButtonVariant.LUMO_SMALL);
            complete.setEnabled(!task.completed());
            return complete;
        }).setHeader("Действие").setWidth("140px").setFlexGrow(0);
        grid.setAllRowsVisible(true);

        return UiComponents.card(new H3("Список задач"), grid);
    }

    private void createTask() {
        if (title.getValue() == null || title.getValue().isBlank()) {
            title.setInvalid(true);
            title.setErrorMessage("Введите название задачи");
            return;
        }

        taskService.createTask(new CreateTaskRequest(title.getValue().trim(), description.getValue()));
        title.clear();
        clearTitleError();
        description.clear();
        refresh();
        Notification.show("Задача добавлена", 2500, Notification.Position.BOTTOM_START)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private void completeTask(Long id) {
        taskService.completeMyTask(id);
        refresh();
        Notification.show("Задача выполнена", 2500, Notification.Position.BOTTOM_START)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private void refresh() {
        List<TaskResponse> tasks = taskService.getMyTasks();
        long completed = tasks.stream()
                .filter(TaskResponse::completed)
                .count();
        long active = tasks.size() - completed;

        stats.removeAll();
        stats.add(
                UiComponents.statCard(VaadinIcon.TASKS, "Всего задач", String.valueOf(tasks.size()), "blue"),
                UiComponents.statCard(VaadinIcon.CLOCK, "В работе", String.valueOf(active), "amber"),
                UiComponents.statCard(VaadinIcon.CHECK_CIRCLE, "Выполнено", String.valueOf(completed), "green")
        );
        grid.setItems(tasks);
    }

    private String normalizeDescription(String value) {
        if (value == null || value.isBlank()) {
            return "Без описания";
        }
        return value;
    }

    private void clearTitleError() {
        title.setInvalid(false);
        title.setErrorMessage(null);
    }
}