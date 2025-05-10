package com.example.todo.data;

import java.util.Date;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "todo_items")
public class TodoEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String description;

	@Enumerated(EnumType.STRING)
	private TaskStatus status;

	private Date createdOn;

	private Date updatedOn;

	private Date deletedOn;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TodoEntity other = (TodoEntity) obj;
		return Objects.equals(description, other.description) && Objects.equals(id, other.id) && status == other.status;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public Date getDeletedOn() {
		return deletedOn;
	}

	public String getDescription() {
		return description;
	}

	public Long getId() {
		return id;
	}

	public TaskStatus getStatus() {
		return status;
	}

	public Date getUpdatedOn() {
		return updatedOn;
	}

	@Override
	public int hashCode() {
		return Objects.hash(description, id, status);
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public void setDeletedOn(Date deletedOn) {
		this.deletedOn = deletedOn;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setStatus(TaskStatus status) {
		this.status = status;
	}

	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}
}
