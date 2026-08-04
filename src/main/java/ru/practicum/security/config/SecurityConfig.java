package ru.practicum.security.config;

import com.vaadin.flow.spring.security.RequestUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
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
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	@Order(1)
	SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
		return http
				.securityMatcher("/api/**")
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(HttpMethod.GET, "/api/public/**").permitAll()
						.requestMatchers("/api/tasks/**", "/api/me").hasRole("USER")
						.requestMatchers("/api/admin/**").hasRole("ADMIN")
						.anyRequest().denyAll()
				)
				.httpBasic(Customizer.withDefaults())
				.sessionManagement(session -> session
						.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				)
				.csrf(AbstractHttpConfigurer::disable)
				.build();
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
	public SecurityFilterChain uiSecurityFilterChain(HttpSecurity http, RequestUtil requestUtil) throws Exception {
		return http
				// правила доступа к страницам интерфейса
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(
								"/",
								"/login",
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