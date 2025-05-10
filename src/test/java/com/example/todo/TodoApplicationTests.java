package com.example.todo;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.example.todo.data.TaskStatus;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class TodoApplicationTests {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@DirtiesContext
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
	void shouldReturnNotFoundOnInvalidId() throws Exception {
		mvc.perform(get("/todo/9999")).andExpect(status().isNotFound());
	}

	@Test
	@DirtiesContext
	void shouldCreateATodoItem() throws Exception {
		String todoText = "Test todo item";
		URI uri = insertATodoItemAndReturnLocation(todoText);

		mvc.perform(get(uri)).andExpect(status().isOk()).andExpect(jsonPath("$.description", is(todoText)))
				.andExpect(jsonPath("$.status", is(TaskStatus.TODO.toString())));
	}

	@Test
	@DirtiesContext
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

	@Test
	@DirtiesContext
	public void shouldDeleteATodoItemSpecifiedById() throws Exception {
		URI uri = insertATodoItemAndReturnLocation("A todo item to test deletion");
		mvc.perform(delete(uri)).andExpect(status().isNoContent());
		mvc.perform(get(uri)).andExpect(status().isNotFound());
	}

	private URI insertATodoItemAndReturnLocation(String todoText) throws Exception {
		TodoCreateRequest request = new TodoCreateRequest(todoText);
		String content = objectMapper.writeValueAsString(request);

		MvcResult result = mvc.perform(post("/todo").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().isCreated())
				.andExpect(header().string(HttpHeaders.LOCATION, startsWith("http://localhost/todo/"))).andReturn();
		String location = result.getResponse().getHeaderValue(HttpHeaders.LOCATION).toString();
		return URI.create(location);
	}
}
