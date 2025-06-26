package org.example.expert.security.exception;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.example.expert.security.jwt.JwtExceptionType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

	private final ObjectMapper objectMapper;

	@Override
	public void commence(
		HttpServletRequest request,
		HttpServletResponse response,
		AuthenticationException authException
	) throws IOException, ServletException {

		//필터에서 설정한 예외 코드 가져오기
		String errorCode = (String)request.getAttribute("exception");

		//필터에서 못 받았으면 authException 에서 가져오기
		if(errorCode == null) {
			errorCode = authException.getMessage();
		}

		//enum 타입으로 변환 시도 (잘못된 값일 경우 기본값 세팅)
		JwtExceptionType jwtExceptionType;
		try{
			//errorCode 의 값으로 변환
			jwtExceptionType = JwtExceptionType.valueOf(errorCode);
		} catch (IllegalArgumentException | NullPointerException e) {
			jwtExceptionType = JwtExceptionType.AUTHENTICATION_REQUIRED;
		}

		//응답값 세팅
		response.setStatus(jwtExceptionType.getStatus());
		response.setContentType("application/json;charset=UTF-8");

		Map<String, String> errorMessage = Map.of("message", jwtExceptionType.getMessage());
		response.getWriter().write(objectMapper.writeValueAsString(errorMessage));

	}

}
