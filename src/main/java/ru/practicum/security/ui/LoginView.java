package ru.practicum.security.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Input;
import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.csrf.CsrfToken;

@Route("login")
@PageTitle("Вход")
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final Paragraph errorMessage = new Paragraph("Проверьте логин и пароль");

    public LoginView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        addClassName("login-page");

        H2 title = new H2("Вход в приложение");
        Paragraph description = new Paragraph("После входа пользователь увидит личные задачи, "
                + "а администратор получит доступ к разделу управления.");

        Div intro = new Div(title, description, createAccountCards());
        intro.addClassName("login-intro");

        Div formPanel = new Div(createLoginForm());
        formPanel.addClassName("login-form-panel");

        Div card = new Div(intro, formPanel);
        card.addClassName("login-card");
        add(card);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (event.getLocation().getQueryParameters().getParameters().containsKey("error")) {
            errorMessage.setVisible(true);
        }
    }

    private Div createAccountCards() {
        Div ivan = accountCard("Обычный пользователь", "ivan", "ivan", "ROLE_USER", "blue");
        Div anna = accountCard("Администратор", "anna", "anna", "ROLE_USER, ROLE_ADMIN", "amber");

        Div accounts = new Div(ivan, anna);
        accounts.addClassName("login-accounts");
        return accounts;
    }

    private Div accountCard(String title, String login, String password, String role, String tone) {
        Span roleBadge = UiComponents.badge(role, tone);
        Span titleLabel = new Span(title);
        titleLabel.addClassName("account-title");

        Div loginRow = accountRow("Логин", login);
        Div passwordRow = accountRow("Пароль", password);

        Div card = new Div(roleBadge, titleLabel, loginRow, passwordRow);
        card.addClassName("account-card");
        return card;
    }

    private Div accountRow(String label, String value) {
        Span labelText = new Span(label);
        labelText.addClassName("account-label");

        Span valueText = new Span(value);
        valueText.addClassName("account-value");

        Div row = new Div(labelText, valueText);
        row.addClassName("account-row");
        return row;
    }

    private Component createLoginForm() {
        H2 title = new H2("Учебные задачи");
        title.addClassName("login-form-title");

        errorMessage.addClassName("login-error");
        errorMessage.setVisible(false);

        NativeButton submit = new NativeButton("Войти");
        submit.getElement().setAttribute("type", "submit");
        submit.addClassName("login-submit");

        NativeLoginForm form = new NativeLoginForm();
        form.add(
                title,
                formField("Логин", "username", "text", "Введите логин"),
                formField("Пароль", "password", "password", "Введите пароль")
        );
        getCsrfToken().ifPresent(token -> form.add(hiddenInput(token.getParameterName(), token.getToken())));
        form.add(errorMessage, submit);
        form.addClassName("login-form");
        return form;
    }

    private Div formField(String label, String name, String type, String placeholder) {
        Span labelText = new Span(label);
        labelText.addClassName("login-label");

        Input input = new Input();
        input.setType(type);
        input.setPlaceholder(placeholder);
        input.getElement().setAttribute("name", name);
        input.addClassName("login-input");

        Div field = new Div(labelText, input);
        field.addClassName("login-field");
        return field;
    }

    private Input hiddenInput(String name, String value) {
        Input input = new Input();
        input.setType("hidden");
        input.setValue(value);
        input.getElement().setAttribute("name", name);
        return input;
    }

    private java.util.Optional<CsrfToken> getCsrfToken() {
        VaadinRequest vaadinRequest = VaadinRequest.getCurrent();
        if (!(vaadinRequest instanceof VaadinServletRequest servletRequest)) {
            return java.util.Optional.empty();
        }

        HttpServletRequest request = servletRequest.getHttpServletRequest();
        return java.util.Optional.ofNullable((CsrfToken) request.getAttribute(CsrfToken.class.getName()));
    }

    @Tag("form")
    private static class NativeLoginForm extends Component implements HasComponents {

        NativeLoginForm() {
            getElement().setAttribute("method", "post");
            getElement().setAttribute("action", "/login");
        }
    }
}