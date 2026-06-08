package ru.practicum.security.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import ru.practicum.security.dto.TaskExampleResponse;
import ru.practicum.security.service.CurrentUserService;
import ru.practicum.security.service.TaskService;

import java.util.List;

@Route(value = "", layout = MainLayout.class)
@PageTitle("Учебные задачи")
public class HomeView extends VerticalLayout {

    public HomeView(TaskService taskService, CurrentUserService currentUserService) {
        VerticalLayout page = UiComponents.page();
        page.add(
                createHero(currentUserService),
                createSecurityCards(),
                createExamples(taskService.getTaskExamples())
        );

        setPadding(false);
        setSpacing(false);
        add(page);
    }

    private Div createHero(CurrentUserService currentUserService) {
        H2 title = new H2("Учебные задачи без хаоса");
        title.addClassName("hero-title");

        Paragraph text = new Paragraph("Небольшое приложение для работы с задачами. "
                + "С помощью него удобно изучать, как Spring Security разделяет публичные страницы, "
                + "пользовательские действия и административные возможности.");
        text.addClassName("hero-text");

        HorizontalLayout actions = new HorizontalLayout();
        actions.addClassName("hero-actions");

        Button tasks = UiComponents.primaryButton("Открыть мои задачи", VaadinIcon.TASKS);
        tasks.addClickListener(event -> getUI().ifPresent(ui -> ui.navigate(TasksView.class)));

        Button login = UiComponents.primaryButton("Войти в приложение", VaadinIcon.SIGN_IN);
        login.addClickListener(event -> getUI().ifPresent(ui -> ui.navigate(LoginView.class)));

        Button admin = new Button("Администрирование", VaadinIcon.USER_STAR.create(),
                event -> getUI().ifPresent(ui -> ui.navigate(AdminView.class)));
        admin.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        if (currentUserService.isCurrentUserAuthenticated()) {
            actions.add(tasks);
            if (currentUserService.hasAuthority("ROLE_ADMIN")) {
                actions.add(admin);
            }
        } else {
            actions.add(login);
        }

        Div hero = new Div(title, text, actions);
        hero.addClassName("hero-card");
        return hero;
    }

    private HorizontalLayout createSecurityCards() {
        Div publicCard = UiComponents.card(
                UiComponents.badge("Без входа", "green"),
                new H3("Публичный каталог"),
                new Paragraph("Главная страница и примеры задач доступны любому посетителю.")
        );

        Div userCard = UiComponents.card(
                UiComponents.badge("ROLE_USER", "blue"),
                new H3("Личные задачи"),
                new Paragraph("Пользователь видит свои задачи, создаёт новые и отмечает выполненные.")
        );

        Div adminCard = UiComponents.card(
                UiComponents.badge("ROLE_ADMIN", "amber"),
                new H3("Администрирование"),
                new Paragraph("Администратор управляет задачами пользователей и примерами заданий.")
        );

        return UiComponents.responsiveRow(publicCard, userCard, adminCard);
    }

    private Div createExamples(List<TaskExampleResponse> examples) {
        Div section = UiComponents.card();

        H3 title = new H3("Примеры учебных задач");
        Paragraph description = new Paragraph("Эти карточки видны без входа. Их удобно использовать "
                + "как публичную часть приложения при проверке правил доступа.");
        description.addClassName("page-description");

        HorizontalLayout cards = UiComponents.responsiveRow(
                examples.stream()
                        .map(this::createExampleCard)
                        .toArray(Div[]::new)
        );

        section.add(title, description, cards);
        return section;
    }

    private Div createExampleCard(TaskExampleResponse example) {
        Span title = new Span(example.title());
        title.addClassName("example-card-title");

        Span description = new Span(example.description());
        description.addClassName("example-card-text");

        Div card = new Div(title, description);
        card.addClassName("app-card");
        return card;
    }
}