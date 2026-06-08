package ru.practicum.security.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;

final class UiComponents {

    private UiComponents() {
    }

    static VerticalLayout page() {
        VerticalLayout page = new VerticalLayout();
        page.addClassName("app-page");
        page.setSpacing(false);
        page.setPadding(false);
        return page;
    }

    static Div card(Component... components) {
        Div card = new Div(components);
        card.addClassName("app-card");
        return card;
    }

    static Div pageHeader(String eyebrow, String title, String description) {
        Span eyebrowLabel = new Span(eyebrow);
        eyebrowLabel.addClassName("eyebrow");

        H2 heading = new H2(title);
        heading.addClassName("page-title");

        Paragraph text = new Paragraph(description);
        text.addClassName("page-description");

        Div header = new Div(eyebrowLabel, heading, text);
        header.addClassName("page-header");
        return header;
    }

    static Div statCard(VaadinIcon icon, String label, String value, String tone) {
        Icon statIcon = icon.create();
        statIcon.addClassNames("stat-icon", tone);

        Span valueLabel = new Span(value);
        valueLabel.addClassName("stat-value");

        Span textLabel = new Span(label);
        textLabel.addClassName("stat-label");

        Div labels = new Div(valueLabel, textLabel);
        labels.addClassName("stat-labels");

        Div card = new Div(statIcon, labels);
        card.addClassName("stat-card");
        return card;
    }

    static Span badge(String text, String tone) {
        Span badge = new Span(text);
        badge.addClassNames("status-badge", tone);
        return badge;
    }

    static Button primaryButton(String text, VaadinIcon icon) {
        Button button = new Button(text, icon.create());
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        return button;
    }

    static RouterLink navLink(String text, Class<? extends Component> target, VaadinIcon icon) {
        RouterLink link = new RouterLink();
        link.add(icon.create(), new Span(text));
        link.setRoute(target);
        link.addClassName("nav-link");
        return link;
    }

    static HorizontalLayout responsiveRow(Component... components) {
        HorizontalLayout row = new HorizontalLayout(components);
        row.addClassName("responsive-row");
        row.setPadding(false);
        row.setSpacing(false);
        return row;
    }
}