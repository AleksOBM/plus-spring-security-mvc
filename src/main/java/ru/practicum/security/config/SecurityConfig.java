package ru.practicum.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.spring.security.RequestUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import ru.practicum.security.dto.ErrorResponse;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	@Bean
	@Order(1)
	SecurityFilterChain apiSecurityFilterChain(HttpSecurity http,
	                                           AuthenticationEntryPoint entryPoint,
	                                           AccessDeniedHandler deniedHandler
	) throws Exception {
		return http
				.securityMatcher("/api/**")
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(HttpMethod.GET, "/api/public/**").permitAll()
						.requestMatchers("/api/tasks/**", "/api/me").hasRole("USER")
						.requestMatchers("/api/admin/**").hasRole("ADMIN")
						.anyRequest().denyAll()
				)
				.httpBasic(Customizer.withDefaults())
				// это чтобы в тестах получить тело ответа в JSON
				.exceptionHandling(ex -> ex
						.authenticationEntryPoint(entryPoint)
						.accessDeniedHandler(deniedHandler)
				)
				.sessionManagement(session -> session
						.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				)
				.csrf(AbstractHttpConfigurer::disable)
				.build();
	}

	@Bean
	AuthenticationEntryPoint restAuthenticationEntryPoint(ObjectMapper mapper) {
		// это чтобы в тестах получить тело ответа в JSON
		return (request, response, ex) -> {
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			response.setContentType("application/json;charset=UTF-8");

			mapper.writeValue(response.getWriter(), new ErrorResponse(
					Instant.now(),
					401,
					"Требуется аутентификация",
					ex.getMessage()
			));
		};
	}

	@Bean
	AccessDeniedHandler restAccessDeniedHandler(ObjectMapper objectMapper) {
		return (request, response, accessDeniedException) -> {
			response.setStatus(HttpStatus.FORBIDDEN.value());
			response.setCharacterEncoding(StandardCharsets.UTF_8.name());
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);

			ErrorResponse error = new ErrorResponse(
					Instant.now(),
					HttpStatus.FORBIDDEN.value(),
					"Доступ запрещен",
					"У вас недостаточно прав для доступа к данному ресурсу"
			);

			objectMapper.writeValue(response.getOutputStream(), error);
		};
	}

	@Bean
	UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
		UserDetails ivan = User.builder()
				.username("ivan")
				.password(passwordEncoder.encode("ivan"))
				.roles("USER")
				.build();

		UserDetails anna = User.builder()
				.username("anna")
				.password(passwordEncoder.encode("anna"))
				.roles("USER", "ADMIN")
				.build();

		return new InMemoryUserDetailsManager(ivan, anna);
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	@Order(2)
	public SecurityFilterChain uiSecurityFilterChain(HttpSecurity http, RequestUtil requestUtil)
			throws Exception {
		return http
				// правила доступа к страницам интерфейса
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(
								"/",
								"/login",
								"/error",
								"/VAADIN/**",
								"/frontend/**",
								"/icons/**",
								"/images/**",
								"/line-awesome/**",
								"/favicon.ico",
								"/manifest.webmanifest",
								"/sw.js",
								"/offline.html"
						).permitAll()
						.requestMatchers("/admin").hasRole("ADMIN")
						.requestMatchers("/tasks").hasRole("USER")
						.anyRequest().authenticated()
				)
				// настройки страницы входа
				.formLogin(login -> login
						.loginPage("/login")
						.defaultSuccessUrl("/tasks", true)
						.permitAll()
				)
				// настройки выхода
				.logout(logout -> logout
						.logoutSuccessUrl("/"))
				// Для интерфейса на Vaadin нужна ещё CSRF-защита
				.csrf(csrf -> csrf
						.ignoringRequestMatchers(requestUtil::isFrameworkInternalRequest)
				)
				.build();
	}

}