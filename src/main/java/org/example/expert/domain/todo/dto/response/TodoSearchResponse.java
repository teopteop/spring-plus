package org.example.expert.domain.todo.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TodoSearchResponse {
	private Long todoId;
	private String title;
	private Long managerCount;
	private Long commentCount;
}
