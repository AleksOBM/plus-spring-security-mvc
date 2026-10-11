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
import com.vaadin.flow.theme.lumo.LumoUtility;
import ru.practicum.security.dto.CreateTaskExampleRequest;
import ru.practicum.security.dto.TaskExampleResponse;
import ru.practicum.security.dto.TaskResponse;
import ru.practicum.security.service.TaskService;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Route(value = "admin", layout = MainLayout.class)
@PageTitle("Администрирование")
public class AdminView extends VerticalLayout {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM HH:mm")
            .withZone(ZoneId.systemDefault());

    private final TaskService taskService;
    private final Grid<TaskResponse> tasksGrid = new Grid<>();
    private final Grid<TaskExampleResponse> examplesGrid = new Grid<>();
    private final TextField exampleTitle = new TextField("Название примера");
    private final TextArea exampleDescription = new TextArea("Описание примера");
    private final HorizontalLayout stats = UiComponents.responsiveRow();

    public AdminView(TaskService taskService) {
        this.taskService = taskService;

        VerticalLayout page = UiComponents.page();
        page.add(
                UiComponents.pageHeader(
                        "Административный раздел",
                        "Управление учебными задачами",
                        "Администратор видит задачи всех пользователей и может поддерживать список примеров."
                ),
                stats,
                createTasksSection(),
                createExamplesSection()
        );

        setPadding(false);
        setSpacing(false);
        add(page);
        refresh();
    }

    private Div createTasksSection() {
        return UiComponents.card(
                new H3("Все задачи пользователей"),
                new Paragraph("Этот список показывает, как роль ADMIN получает доступ к данным всего приложения."),
                createTasksGrid()
        );
    }

    private Grid<TaskResponse> createTasksGrid() {
        tasksGrid.addClassName("tasks-grid");
        tasksGrid.addColumn(TaskResponse::id).setHeader("ID").setWidth("90px").setFlexGrow(0);
        tasksGrid.addColumn(TaskResponse::ownerUsername).setHeader("Пользователь");
        tasksGrid.addColumn(TaskResponse::title).setHeader("Название").setFlexGrow(1);
        tasksGrid.addColumn(task -> DATE_FORMATTER.format(task.createdAt()))
                .setHeader("Создана")
                .setWidth("130px")
                .setFlexGrow(0);
        tasksGrid.addComponentColumn(task -> task.completed()
                ? UiComponents.badge("Выполнена", "green")
                : UiComponents.badge("В работе", "amber")
        ).setHeader("Статус").setWidth("140px").setFlexGrow(0);
        tasksGrid.addComponentColumn(task -> {
            Button delete = new Button("Удалить", VaadinIcon.TRASH.create(), event -> deleteTask(task.id()));
            delete.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
            return delete;
        }).setHeader("Действие").setWidth("140px").setFlexGrow(0);
        tasksGrid.setAllRowsVisible(true);
        return tasksGrid;
    }

    private Div createExamplesSection() {
        return UiComponents.card(
                new H3("Примеры учебных задач"),
                new Paragraph("Примеры видны на публичной главной странице. Их можно добавлять из админского раздела."),
                createExampleForm(),
                createExamplesGrid()
        );
    }

    private HorizontalLayout createExampleForm() {
        exampleTitle.setWidth("320px");
        exampleTitle.setRequiredIndicatorVisible(true);
        exampleTitle.addValueChangeListener(event -> clearExampleTitleError());
        exampleDescription.setWidth("420px");

        Button add = new Button("Добавить пример", VaadinIcon.PLUS.create(), event -> addExample());
        add.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout form = new HorizontalLayout(exampleTitle, exampleDescription, add);
        form.addClassName("form-row");
        form.setAlignItems(Alignment.END);
        form.addClassNames(LumoUtility.Padding.Bottom.MEDIUM);
        return form;
    }

    private Grid<TaskExampleResponse> createExamplesGrid() {
        examplesGrid.addClassName("examples-grid");
        examplesGrid.addColumn(TaskExampleResponse::id).setHeader("ID").setWidth("90px").setFlexGrow(0);
        examplesGrid.addColumn(TaskExampleResponse::title).setHeader("Название").setFlexGrow(1);
        examplesGrid.addColumn(TaskExampleResponse::description).setHeader("Описание").setFlexGrow(2);
        examplesGrid.setAllRowsVisible(true);
        return examplesGrid;
    }

    private void deleteTask(Long id) {
        taskService.deleteTask(id);
        refresh();
        Notification.show("Задача удалена", 2500, Notification.Position.BOTTOM_START)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private void addExample() {
        if (exampleTitle.getValue() == null || exampleTitle.getValue().isBlank()) {
            exampleTitle.setInvalid(true);
            exampleTitle.setErrorMessage("Введите название примера");
            return;
        }

        taskService.addTaskExample(new CreateTaskExampleRequest(
                exampleTitle.getValue().trim(),
                exampleDescription.getValue()
        ));
        exampleTitle.clear();
        clearExampleTitleError();
        exampleDescription.clear();
        refresh();
        Notification.show("Пример добавлен", 2500, Notification.Position.BOTTOM_START)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private void refresh() {
        List<TaskResponse> tasks = taskService.getAllTasks();
        long completed = tasks.stream()
                .filter(TaskResponse::completed)
                .count();
        long active = tasks.size() - completed;
        long users = tasks.stream()
                .map(TaskResponse::ownerUsername)
                .distinct()
                .count();

        stats.removeAll();
        stats.add(
                UiComponents.statCard(VaadinIcon.USERS,
                        "Пользователей", String.valueOf(users), "blue"),
                UiComponents.statCard(VaadinIcon.CLOCK,
                        "Задач в работе", String.valueOf(active), "amber"),
                UiComponents.statCard(VaadinIcon.CHECK_CIRCLE,
                        "Выполнено", String.valueOf(completed), "green")
        );

        tasksGrid.setItems(tasks);
        examplesGrid.setItems(taskService.getTaskExamples());
    }

    private void clearExampleTitleError() {
        exampleTitle.setInvalid(false);
        exampleTitle.setErrorMessage(null);
    }
}