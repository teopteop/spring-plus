package org.example.expert.security.jwt;

import lombok.Getter;

@Getter
public enum JwtExceptionType {

	INVALID_SIGNATURE("잘못된 JWT 서명입니다.", 401),
	MALFORMED_TOKEN("잘못된 JWT 토큰입니다.", 401),
	EXPIRED_TOKEN("만료된 토큰입니다.", 401),
	UNSUPPORTED_TOKEN("지원되지 않는 형식의 토큰입니다.", 400),
	EMPTY_TOKEN("토큰이 비어있습니다.", 400),
	AUTHENTICATION_REQUIRED("로그인(인증)이 필요합니다.", 401);

	private final String message;
	private final int status;

	JwtExceptionType(String message, int status) {
		this.message = message;
		this.status = status;
	}

}
