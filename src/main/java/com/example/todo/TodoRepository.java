package com.example.todo;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.example.todo.data.TodoEntity;

public interface TodoRepository extends CrudRepository<TodoEntity, Long>, PagingAndSortingRepository<TodoEntity, Long> {

	@Query("SELECT todo FROM TodoEntity todo WHERE todo.deletedOn IS NULL")
	public Page<TodoEntity> findAllExistingTodos(Pageable pageable);
	
	@Query("SELECT todo FROM TodoEntity todo WHERE todo.id = ?1 AND todo.deletedOn IS NULL")
	public Optional<TodoEntity> findByIdIfNotDeleted(Long id);
}
