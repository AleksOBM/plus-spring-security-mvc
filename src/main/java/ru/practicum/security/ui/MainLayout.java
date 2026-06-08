package ru.practicum.security.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinResponse;
import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.flow.server.VaadinServletResponse;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import ru.practicum.security.dto.CurrentUserResponse;
import ru.practicum.security.service.CurrentUserService;

public class MainLayout extends AppLayout {

    private final CurrentUserService currentUserService;

    public MainLayout(CurrentUserService currentUserService) {
        this.currentUserService = currentUserService;

        setPrimarySection(Section.DRAWER);
        addToNavbar(createHeader());
        addToDrawer(createNavigation());
    }

    private Component createHeader() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Открыть меню");

        H1 title = new H1("Учебные задачи");
        title.addClassNames("app-title", LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        HorizontalLayout header = new HorizontalLayout(toggle, title, createUserBlock());
        header.setWidthFull();
        header.expand(title);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.addClassNames("app-header", LumoUtility.Padding.Horizontal.MEDIUM);
        return header;
    }

    private Component createNavigation() {
        Span title = new Span("Task Manager");
        title.addClassName("drawer-brand-title");

        Span subtitle = new Span("Spring Security MVC");
        subtitle.addClassName("drawer-brand-subtitle");

        Div brand = new Div(title, subtitle);
        brand.addClassName("drawer-brand");

        VerticalLayout navigation = new VerticalLayout();
        navigation.addClassName("nav-list");
        navigation.setPadding(true);
        navigation.setSpacing(false);

        navigation.add(UiComponents.navLink("Главная", HomeView.class, VaadinIcon.HOME));
        if (currentUserService.isCurrentUserAuthenticated()) {
            navigation.add(UiComponents.navLink("Мои задачи", TasksView.class, VaadinIcon.TASKS));
        }
        if (currentUserService.hasAuthority("ROLE_ADMIN")) {
            navigation.add(UiComponents.navLink("Администрирование", AdminView.class, VaadinIcon.USER_STAR));
        }
        if (!currentUserService.isCurrentUserAuthenticated()) {
            navigation.add(UiComponents.navLink("Войти", LoginView.class, VaadinIcon.SIGN_IN));
        }

        return new VerticalLayout(brand, navigation);
    }

    private Component createUserBlock() {
        if (!currentUserService.isCurrentUserAuthenticated()) {
            Button login = new Button("Войти", VaadinIcon.SIGN_IN.create(),
                    event -> getUI().ifPresent(ui -> ui.navigate(LoginView.class)));
            login.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
            return login;
        }

        CurrentUserResponse user = currentUserService.getCurrentUser();
        Icon userIcon = VaadinIcon.USER.create();
        userIcon.setSize("18px");

        Span name = new Span(user.username());
        name.addClassName("user-name");

        Span roles = new Span(roleLabel(user));
        roles.addClassName("user-role");

        Div labels = new Div(name, roles);

        HorizontalLayout layout = new HorizontalLayout(userIcon, labels, createLogoutButton());
        layout.addClassName("user-pill");
        layout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        return layout;
    }

    private String roleLabel(CurrentUserResponse user) {
        if (user.authorities().contains("ROLE_ADMIN")) {
            return "Администратор";
        }
        return "Пользователь";
    }

    private Component createLogoutButton() {
        Button logout = new Button("Выйти", event -> event.getSource().getUI()
                .ifPresent(this::logoutCurrentUser));
        logout.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE, ButtonVariant.LUMO_SMALL);
        logout.addClassName("logout-button");
        return logout;
    }

    private void logoutCurrentUser(UI ui) {
        VaadinRequest vaadinRequest = VaadinRequest.getCurrent();
        VaadinResponse vaadinResponse = VaadinResponse.getCurrent();
        if (vaadinRequest instanceof VaadinServletRequest servletRequest
                && vaadinResponse instanceof VaadinServletResponse servletResponse) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            new SecurityContextLogoutHandler().logout(
                    servletRequest.getHttpServletRequest(),
                    servletResponse.getHttpServletResponse(),
                    authentication
            );
        }

        ui.getPage().setLocation("/login");
    }
}