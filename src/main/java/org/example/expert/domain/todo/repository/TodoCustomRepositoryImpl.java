package org.example.expert.domain.todo.repository;

import static org.example.expert.domain.comment.entity.QComment.*;
import static org.example.expert.domain.manager.entity.QManager.*;
import static org.example.expert.domain.todo.entity.QTodo.*;
import static org.example.expert.domain.user.entity.QUser.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.example.expert.domain.comment.entity.QComment;
import org.example.expert.domain.manager.entity.QManager;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class TodoCustomRepositoryImpl implements TodoCustomRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public Optional<Todo> findByIdWithUser(Long id) {
		return Optional.ofNullable(
			queryFactory
			.selectFrom(todo)
			.join(todo.user, user).fetchJoin()
			.where(todo.id.eq(id))
			.fetchOne()
		);
	}

	@Override
	public Page<TodoSearchResponse> searchTodosInfo(Pageable pageable, String title, String nickname,
		LocalDate periodStart, LocalDate periodEnd) {

		List<TodoSearchResponse> contents = queryFactory
			.select(
				Projections.constructor(
				TodoSearchResponse.class,
				todo.id,
				todo.title,
				manager.id.countDistinct(),
				comment.id.countDistinct()
			))
			.from(todo)
			.leftJoin(todo.managers, manager)
			.leftJoin(todo.comments, comment)
			.where(
				titleContains(title),
				nicknameContains(nickname),
				periodBetween(periodStart, periodEnd)
			)
			.groupBy(todo.id)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		//카운팅이 0일시 NPE 방지 목적 옵셔널로 감싸기
		Long total = Optional.ofNullable(
			queryFactory
			.select(todo.id.countDistinct())
			.from(todo)
			.leftJoin(todo.managers, manager)
			.where(
				titleContains(title),
				nicknameContains(nickname),
				periodBetween(periodStart, periodEnd)
			)
			.fetchOne()
		).orElse(0L);

		return new PageImpl<>(contents, pageable, total);
	}

	private BooleanExpression titleContains(String title) {
		return StringUtils.hasText(title) ? todo.title.contains(title) : null;
	}

	private BooleanExpression nicknameContains(String nickname) {
		return StringUtils.hasText(nickname) ? todo.user.nickname.contains(nickname) : null;
	}

	private BooleanExpression periodGoe(LocalDate periodStart) {
		return periodStart != null ? todo.createdAt.goe(periodStart.atStartOfDay()) : null;
	}

	private BooleanExpression periodLoe(LocalDate periodEnd) {
		return periodEnd != null ? todo.createdAt.loe(periodEnd.atTime(LocalTime.MAX)) : null;
	}

	private BooleanExpression periodBetween(LocalDate periodStart, LocalDate periodEnd) {
		//LocalDate 에 시간을 붙여 LocalDateTime 으로 변환, null 일때 null 값 세팅
		LocalDateTime startDateTime = periodStart != null ? periodStart.atStartOfDay() : null;
		LocalDateTime endDateTime = periodEnd != null ? periodEnd.atTime(LocalTime.MAX) : null;

		if (startDateTime != null && endDateTime != null) {
			return todo.createdAt.between(startDateTime, endDateTime);
		} else if (startDateTime != null) {
			return todo.createdAt.goe(startDateTime);
		} else if (endDateTime != null) {
			return todo.createdAt.loe(endDateTime);
		}
		return null;
	}
}
