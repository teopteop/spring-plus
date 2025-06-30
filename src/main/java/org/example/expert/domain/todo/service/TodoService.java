package org.example.expert.domain.todo.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.client.WeatherClient;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.common.exception.UserNotFoundException;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.request.TodoSearchRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.repository.UserRepository;
import org.example.expert.security.userDetails.CustomUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TodoService {

    private final TodoRepository todoRepository;
    private final UserRepository userRepository;
    private final WeatherClient weatherClient;

    public TodoSaveResponse saveTodo(CustomUserDetails userDetails, TodoSaveRequest todoSaveRequest) {
        User user = userRepository.findById(userDetails.getId())
            .orElseThrow(() -> new UserNotFoundException("인증객체와 일치하는 유저가 없습니다."));

        String weather = weatherClient.getTodayWeather();

        Todo newTodo = new Todo(
                todoSaveRequest.getTitle(),
                todoSaveRequest.getContents(),
                weather,
                user
        );

        Todo savedTodo = todoRepository.save(newTodo);

        return new TodoSaveResponse(
                savedTodo.getId(),
                savedTodo.getTitle(),
                savedTodo.getContents(),
                weather,
                UserResponse.of(user.getId(), user.getEmail(), user.getNickname())
        );
    }

    public Page<TodoResponse> getTodos(int page, int size, String weather) {
        Pageable pageable = PageRequest.of(page - 1, size);

        Page<Todo> todos = todoRepository.findAllByOrderByModifiedAtDesc(weather, pageable);

        return todos.map(todo -> TodoResponse.of(
                todo.getId(),
                todo.getTitle(),
                todo.getContents(),
                todo.getWeather(),
                UserResponse.of(todo.getUser().getId(), todo.getUser().getEmail(), todo.getUser().getNickname()),
                todo.getCreatedAt(),
                todo.getModifiedAt()
        ));
    }

    public TodoResponse getTodo(long todoId) {
        Todo todo = todoRepository.findByIdWithUser(todoId)
                .orElseThrow(() -> new InvalidRequestException("Todo not found"));

        User user = todo.getUser();

        return TodoResponse.of(
                todo.getId(),
                todo.getTitle(),
                todo.getContents(),
                todo.getWeather(),
                UserResponse.of(user.getId(), user.getEmail(), user.getNickname()),
                todo.getCreatedAt(),
                todo.getModifiedAt()
        );
    }

    public Page<TodoSearchResponse> findTodosBySearch(Pageable pageable, TodoSearchRequest request) {
        return todoRepository.searchTodosInfo(
            pageable,
            request.getTitle(),
            request.getNickname(),
            request.getPeriodStart(),
            request.getPeriodEnd()
        );
    }

}
