package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo, Long>, TodoCustomRepository {

    @Query("SELECT t FROM Todo t LEFT JOIN FETCH t.user u WHERE (:weather IS NULL OR t.weather = :weather) ORDER BY t.modifiedAt DESC")
    Page<Todo> findAllByOrderByModifiedAtDesc(@Param("weather") String weather, Pageable pageable );

    // @Query("SELECT t FROM Todo t " +
    //         "LEFT JOIN t.user " +
    //         "WHERE t.id = :todoId")
    // Optional<Todo> findByIdWithUser(@Param("todoId") Long todoId);


}
