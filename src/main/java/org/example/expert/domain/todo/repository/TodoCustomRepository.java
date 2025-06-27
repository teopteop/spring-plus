package org.example.expert.domain.todo.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TodoCustomRepository {

	Optional<Todo> findByIdWithUser(Long id);

	Page<TodoSearchResponse> searchTodosInfo(
		Pageable pageable, String title, String nickname, LocalDate periodStart, LocalDate periodEnd
	);
}
