package dev.anandbose.todo.resource;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import dev.anandbose.todo.resource.data.TodoEntity;

public interface TodoRepository extends CrudRepository<TodoEntity, Long>, PagingAndSortingRepository<TodoEntity, Long> {

	@Query("SELECT todo FROM TodoEntity todo WHERE todo.userId = ?1 AND todo.deletedOn IS NULL")
	public Page<TodoEntity> findAllExistingTodos(String userId, Pageable page);
	
	@Query("SELECT todo FROM TodoEntity todo WHERE todo.userId = ?1 AND todo.id = ?2 AND todo.deletedOn IS NULL")
	public Optional<TodoEntity> findByIdIfNotDeleted(String userId, Long id);
}
