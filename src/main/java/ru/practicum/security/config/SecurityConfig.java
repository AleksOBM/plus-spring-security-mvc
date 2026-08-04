package ru.practicum.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
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

}