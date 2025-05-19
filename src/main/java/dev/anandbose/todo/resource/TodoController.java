package dev.anandbose.todo.resource;

import java.net.URI;
import java.security.Principal;
import java.util.Calendar;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import dev.anandbose.todo.resource.data.TaskStatus;
import dev.anandbose.todo.resource.data.TodoCreateRequest;
import dev.anandbose.todo.resource.data.TodoEntity;
import dev.anandbose.todo.resource.data.TodoResponse;
import dev.anandbose.todo.resource.data.TodoUpdateRequest;

@RestController
@RequestMapping("/todo")
public class TodoController {

	private final TodoRepository repository;

	public TodoController(TodoRepository repository) {
		this.repository = repository;
	}

	@GetMapping
	public ResponseEntity<PagedModel<TodoResponse>> getAllTodos(Pageable pageable, Principal principal) {
		Page<TodoResponse> todos = repository.findAllExistingTodos(principal.getName(), pageable)
				.map(TodoResponse::fromTodoEntity);
		PagedModel<TodoResponse> model = new PagedModel<>(todos);
		return ResponseEntity.ok(model);
	}

	@GetMapping("/{id}")
	public ResponseEntity<TodoResponse> getTodoById(@PathVariable Long id, Principal principal) {
		return repository.findByIdIfNotDeleted(principal.getName(), id)
				.map(item -> ResponseEntity.ok(TodoResponse.fromTodoEntity(item)))
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@PatchMapping("/{id}")
	public ResponseEntity<Void> updateTodoItem(@PathVariable Long id, @RequestBody TodoUpdateRequest request,
			Principal principal) {
		Optional<TodoEntity> todoEntityOptional = repository.findByIdIfNotDeleted(principal.getName(), id);
		if (todoEntityOptional.isPresent()) {
			TodoEntity entity = todoEntityOptional.get();
			String description = request.description();
			TaskStatus status = request.status();
			boolean isUpdated = false;
			if (description != null) {
				entity.setDescription(description);
				isUpdated = true;
			}
			if (status != null) {
				entity.setStatus(status);
				isUpdated = true;
			}
			if (isUpdated) {
				entity.setUpdatedOn(Calendar.getInstance().getTime());
			}
			repository.save(entity);
		} else {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.noContent().build();
	}

	@PostMapping
	public ResponseEntity<Void> createTodoItem(@RequestBody TodoCreateRequest request, UriComponentsBuilder ucb,
			Principal principal) {
		TodoEntity todo = new TodoEntity();
		todo.setDescription(request.description());
		todo.setStatus(TaskStatus.TODO);
		todo.setCreatedOn(Calendar.getInstance().getTime());
		todo.setUpdatedOn(Calendar.getInstance().getTime());
		todo.setUserId(principal.getName());
		todo = repository.save(todo);
		URI location = ucb.path("todo/{id}").buildAndExpand(todo.getId()).toUri();
		return ResponseEntity.created(location).build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteTodoItem(@PathVariable Long id, Principal principal) {
		Optional<TodoEntity> entityOptional = repository.findByIdIfNotDeleted(principal.getName(), id);
		if (entityOptional.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		TodoEntity entity = entityOptional.get();
		entity.setDeletedOn(Calendar.getInstance().getTime());
		repository.save(entity);
		return ResponseEntity.noContent().build();
	}
}
