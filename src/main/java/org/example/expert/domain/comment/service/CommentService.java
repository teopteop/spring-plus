package org.example.expert.domain.comment.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.comment.dto.request.CommentSaveRequest;
import org.example.expert.domain.comment.dto.response.CommentResponse;
import org.example.expert.domain.comment.dto.response.CommentSaveResponse;
import org.example.expert.domain.comment.entity.Comment;
import org.example.expert.domain.comment.repository.CommentRepository;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.common.exception.UserNotFoundException;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.repository.UserRepository;
import org.example.expert.security.userDetails.CustomUserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final TodoRepository todoRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommentSaveResponse saveComment(CustomUserDetails userDetails, long todoId, CommentSaveRequest commentSaveRequest) {
        User user = userRepository.findById(userDetails.getId())
            .orElseThrow(() -> new UserNotFoundException("인증객체와 일치하는 유저가 없습니다."));

        Todo todo = todoRepository.findById(todoId).orElseThrow(() ->
                new InvalidRequestException("Todo not found"));

        Comment newComment = new Comment(
                commentSaveRequest.getContents(),
                user,
                todo
        );

        Comment savedComment = commentRepository.save(newComment);

        return new CommentSaveResponse(
                savedComment.getId(),
                savedComment.getContents(),
                UserResponse.of(user.getId(), user.getEmail(), user.getNickname())
        );
    }

    public List<CommentResponse> getComments(long todoId) {
        List<Comment> commentList = commentRepository.findByTodoIdWithUser(todoId);

        List<CommentResponse> dtoList = new ArrayList<>();
        for (Comment comment : commentList) {
            User user = comment.getUser();
            CommentResponse dto = new CommentResponse(
                    comment.getId(),
                    comment.getContents(),
                    UserResponse.of(user.getId(), user.getEmail(), user.getNickname())
            );
            dtoList.add(dto);
        }
        return dtoList;
    }
}
