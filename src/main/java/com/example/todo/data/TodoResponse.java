package com.example.todo.data;

import java.util.Date;

public record TodoResponse(Long id, String description, TaskStatus status, Date createdOn, Date updatedOn) {
	public static TodoResponse fromTodoEntity(TodoEntity entity) {
		return new TodoResponse(entity.getId(), entity.getDescription(), entity.getStatus(), entity.getCreatedOn(),
				entity.getUpdatedOn());
	}
}
