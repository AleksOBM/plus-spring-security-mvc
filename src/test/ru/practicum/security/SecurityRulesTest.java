package ru.practicum.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = TaskManagerApp.class)
@AutoConfigureMockMvc
class SecurityRulesTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void publicInfo_isAvailableWithoutAuthentication() throws Exception {
		mockMvc.perform(get("/api/public/info"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name")
						.value("Демо Spring Security для Spring MVC"));
	}

	@Test
	void userEndpoint_withoutAuthentication_isUnauthorized() throws Exception {
		mockMvc.perform(get("/api/tasks/my"))
				.andExpect(status().isUnauthorized())
				.andExpect(header().doesNotExist("Location"))
				.andExpect(jsonPath("$.status").value(401))
				.andExpect(jsonPath("$.error")
						.value("Требуется аутентификация"))
				.andDo(print());
	}

	@Test
	void userCanCreateOwnTask() throws Exception {
		mockMvc.perform(post("/api/tasks")
						.with(httpBasic("ivan", "ivan"))
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
							{
							  "title": "Потренироваться с Basic Authentication",
							  "description": "Передать учетные данные в заголовке Authorization."
							}
							"""))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.ownerUsername").value("ivan"))
				.andExpect(jsonPath("$.completed").value(false));
	}

	@Test
	void regularUser_cannotOpenAdminEndpoint() throws Exception {
		mockMvc.perform(get("/api/admin/tasks")
						.with(httpBasic("ivan", "ivan")))
				.andExpect(status().isForbidden())
				.andExpect(jsonPath("$.status").value(403))
				.andExpect(jsonPath("$.error").value("Доступ запрещен"));
	}

	@Test
	void admin_canOpenAdminEndpoint() throws Exception {
		mockMvc.perform(get("/api/admin/tasks")
						.with(httpBasic("anna", "anna")))
				.andExpect(status().isOk());
	}

	@Test
	void currentUserEndpoint_returnsAuthenticationDetails() throws Exception {
		mockMvc.perform(get("/api/me")
						.with(httpBasic("ivan", "ivan")))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.username").value("ivan"))
				.andExpect(jsonPath("$.authenticated").value(true))
				.andExpect(jsonPath("$.authorities", hasItem("ROLE_USER")));
	}

	@Test
	void uiTasksPage_withoutAuthentication_redirectsToLoginPage() throws Exception {
		mockMvc.perform(get("/tasks"))
				.andExpect(status().is3xxRedirection())
				.andExpect(header().string("Location", "http://localhost/login"));
	}

	@Test
	void uiLogin_withValidCredentialsAndCsrf_redirectsToTasksPage() throws Exception {
		mockMvc.perform(post("/login")
						.param("username", "ivan")
						.param("password", "ivan")
						.with(csrf()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/tasks"));
	}

	@Test
	void uiLogin_withoutCsrf_isForbidden() throws Exception {
		mockMvc.perform(post("/login")
						.param("username", "ivan")
						.param("password", "ivan"))
				.andExpect(status().isForbidden());
	}

	@Test
	void uiAdminPage_regularUser_isForbidden() throws Exception {
		mockMvc.perform(get("/admin")
						.with(user("ivan").roles("USER")))
				.andExpect(status().isForbidden());
	}

}