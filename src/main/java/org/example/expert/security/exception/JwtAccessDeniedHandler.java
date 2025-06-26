package org.example.expert.security.exception;

import java.io.IOException;
import java.util.Map;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

	private final ObjectMapper objectMapper;

	@Override
	public void handle(
		HttpServletRequest request,
		HttpServletResponse response,
		AccessDeniedException accessDeniedException
	) throws IOException, ServletException {

		//응답값 세팅
		response.setStatus(HttpServletResponse.SC_FORBIDDEN); //403에러
		response.setContentType("application/json;charset=UTF-8");

		Map<String, String> errorMessage = Map.of("message", accessDeniedException.getMessage());
		response.getWriter().write(objectMapper.writeValueAsString(errorMessage));
		log.debug("디나이드핸들러 uri: {}", request.getRequestURI());

	}
}
