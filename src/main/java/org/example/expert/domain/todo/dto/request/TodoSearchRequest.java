package org.example.expert.domain.todo.dto.request;

import java.time.LocalDate;

import lombok.Getter;

@Getter
public class TodoSearchRequest {
	private String title;
	private String nickname;
	private LocalDate periodStart;
	private LocalDate periodEnd;
}
