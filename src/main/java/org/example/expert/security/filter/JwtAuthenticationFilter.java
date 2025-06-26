package org.example.expert.security.filter;

import java.io.IOException;

import org.example.expert.security.jwt.JwtExceptionType;
import org.example.expert.security.jwt.TokenProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final TokenProvider tokenProvider;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		//request 에서 토큰 추출
		String token = tokenProvider.resolveToken(request);

		//토큰이 비어있으면 바로 다음 필터로 진행
		if(token == null) {
			log.debug("토큰이 비어있습니다. uri: {}", request.getRequestURI());
			request.setAttribute("exception", JwtExceptionType.EMPTY_TOKEN.name());
			filterChain.doFilter(request, response);
			return;
		}

		try {
			tokenProvider.validateToken(token);
			Authentication authentication = tokenProvider.getAuthentication(token);
			SecurityContextHolder.getContext().setAuthentication(authentication);
		} catch (SecurityException e) {
			request.setAttribute("exception", JwtExceptionType.INVALID_SIGNATURE.name());
		} catch (MalformedJwtException e) {
			request.setAttribute("exception", JwtExceptionType.MALFORMED_TOKEN.name());
		} catch (ExpiredJwtException e) {
			request.setAttribute("exception", JwtExceptionType.EXPIRED_TOKEN.name());
		} catch (UnsupportedJwtException e) {
			request.setAttribute("exception", JwtExceptionType.UNSUPPORTED_TOKEN.name());
		} catch (IllegalArgumentException e) {
			request.setAttribute("exception", JwtExceptionType.EMPTY_TOKEN.name());
		}

		filterChain.doFilter(request, response);
	}
}
