package com.example.todo;

import com.example.todo.data.TaskStatus;

public record TodoUpdateRequest(String description, TaskStatus status) {

}
