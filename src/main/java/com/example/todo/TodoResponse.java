package com.example.todo;

import java.util.Date;

import com.example.todo.data.TaskStatus;
import com.example.todo.data.TodoEntity;

public record TodoResponse(Long id, String description, TaskStatus status, Date createdOn, Date updatedOn) {
	public static TodoResponse fromTodoEntity(TodoEntity entity) {
		return new TodoResponse(entity.getId(), entity.getDescription(), entity.getStatus(), entity.getCreatedOn(),
				entity.getUpdatedOn());
	}
}
