![Static Badge](https://img.shields.io/badge/Java-21-green)
![Static Badge](https://img.shields.io/badge/Spring_Boot-3.3.2-green)
![Static Badge](https://img.shields.io/badge/MVC-1a9d66)
![Static Badge](https://img.shields.io/badge/SpringSequrity-538681)
![Static Badge](https://img.shields.io/badge/Mockito-green)
![Static Badge](https://img.shields.io/badge/Maven-orange)
![Static Badge](https://img.shields.io/badge/Monolith-8A2BE2)

# Менеджер учебных задач

**Учебный проект**

Spring MVC-приложение для знакомства с базовыми возможностями Spring Security.

В проекте есть REST API и браузерный интерфейс менеджера задач. Приложение показывает:

- Basic Auth для REST API;
- вход через форму для браузерного интерфейса;
- роли USER и ADMIN;
- ответы 401 Unauthorized и 403 Forbidden;
- HTTP-сессию и CSRF-защиту;
- тестирование правил доступа через MockMvc.

## Пользователи

| Логин | Пароль | Роли |
|---|---|---|
| ivan | ivan | USER |
| anna | anna | USER, ADMIN |

## Запуск

```bash
mvn spring-boot:run
```

После запуска приложение доступно по адресу:

text http://localhost:8080

## Проверка

REST API можно проверять через Postman или curl с Basic Auth.

Браузерный интерфейс позволяет войти в приложение, работать со своими задачами и открыть административный раздел под пользователем `anna`.