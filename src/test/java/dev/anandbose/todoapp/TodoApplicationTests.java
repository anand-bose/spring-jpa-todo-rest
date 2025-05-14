package dev.anandbose.todoapp;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URI;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;

import dev.anandbose.todoapp.data.TaskStatus;
import dev.anandbose.todoapp.data.TodoCreateRequest;
import dev.anandbose.todoapp.data.TodoUpdateRequest;

@SpringBootTest
@AutoConfigureMockMvc
class TodoApplicationTests {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ObjectMapper objectMapper;

	private URI insertATodoItemAndReturnLocation(String todoText) throws Exception {
		TodoCreateRequest request = new TodoCreateRequest(todoText);
		String content = objectMapper.writeValueAsString(request);

		MvcResult result = mvc.perform(post("/todo").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().isCreated())
				.andExpect(header().string(HttpHeaders.LOCATION, startsWith("http://localhost/todo/"))).andReturn();
		String location = result.getResponse().getHeaderValue(HttpHeaders.LOCATION).toString();
		return URI.create(location);
	}

	@Test
	@DirtiesContext
	@WithMockUser(username = "anand", password = "abc123")
	void shouldCreateATodoItem() throws Exception {
		String todoText = "Test todo item";
		URI uri = insertATodoItemAndReturnLocation(todoText);

		mvc.perform(get(uri)).andExpect(status().isOk()).andExpect(jsonPath("$.description", is(todoText)))
				.andExpect(jsonPath("$.status", is(TaskStatus.TODO.toString())));
	}

	@Test
	@DirtiesContext
	@WithMockUser(username = "anand", password = "abc123")
	public void shouldDeleteATodoItemSpecifiedById() throws Exception {
		URI uri = insertATodoItemAndReturnLocation("A todo item to test deletion");
		mvc.perform(delete(uri)).andExpect(status().isNoContent());
		mvc.perform(get(uri)).andExpect(status().isNotFound());
	}

	@Test
	public void shouldExpectUnauthenticatedIfNoAuthorizationIsProvided() throws Exception {
		mvc.perform(get("/todo")).andExpect(status().isUnauthorized());
	}

	@Test
	@WithMockUser(username = "anand", password = "abc123")
	@DirtiesContext
	public void shouldNotAccessOthersTodoItems() throws Exception {
		String todoText = "Test todo item";
		URI uri = insertATodoItemAndReturnLocation(todoText);

		mvc.perform(get(uri).with(httpBasic("meera", "xyz1234"))).andExpect(status().isNotFound());

		mvc.perform(get("/todo").with(httpBasic("meera", "xyz1234"))).andExpect(status().isOk())
				.andExpect(jsonPath("$.content.length()", is(0)));
	}

	@Test
	@DirtiesContext
	@WithMockUser(username = "anand", password = "abc123")
	void shouldReturnAllTodoItems() throws Exception {
		List<String> todoItems = List.of("Todo 1", "Todo 2", "Todo 3", "Todo 4", "Todo 5");
		for (String todoItem : todoItems) {
			insertATodoItemAndReturnLocation(todoItem);
		}
		mvc.perform(get("/todo").queryParam("page", "0").queryParam("size", "3")).andExpect(status().isOk())
				.andExpect(jsonPath("$.content.length()", is(3))).andExpect(jsonPath("$.page.number", is(0)));
		mvc.perform(get("/todo").queryParam("page", "1").queryParam("size", "3")).andExpect(status().isOk())
				.andExpect(jsonPath("$.content.length()", is(2))).andExpect(jsonPath("$.page.number", is(1)));
	}

	@Test
	@WithMockUser(username = "anand", password = "abc123")
	void shouldReturnNotFoundOnInvalidId() throws Exception {
		mvc.perform(get("/todo/9999")).andExpect(status().isNotFound());
	}

	@Test
	@DirtiesContext
	@WithMockUser(username = "anand", password = "abc123")
	void shouldUpdateATodoItem() throws Exception {
		String todoText = "Test todo item 2";
		String updatedTodoText = "Test todo item 3";
		URI uri = insertATodoItemAndReturnLocation(todoText);

		mvc.perform(get(uri)).andExpect(status().isOk()).andExpect(jsonPath("$.description", is(todoText)))
				.andExpect(jsonPath("$.status", is(TaskStatus.TODO.toString())));

		TodoUpdateRequest descriptionUpdateRequest = new TodoUpdateRequest(updatedTodoText, null);
		mvc.perform(patch(uri).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(descriptionUpdateRequest))).andExpect(status().isNoContent());

		mvc.perform(get(uri)).andExpect(status().isOk()).andExpect(jsonPath("$.description", is(updatedTodoText)))
				.andExpect(jsonPath("$.status", is(TaskStatus.TODO.toString())));

		TodoUpdateRequest statusUpdateRequest = new TodoUpdateRequest(null, TaskStatus.DONE);
		mvc.perform(patch(uri).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(statusUpdateRequest))).andExpect(status().isNoContent());

		mvc.perform(get(uri)).andExpect(status().isOk()).andExpect(jsonPath("$.description", is(updatedTodoText)))
				.andExpect(jsonPath("$.status", is(TaskStatus.DONE.toString())));
	}
}
