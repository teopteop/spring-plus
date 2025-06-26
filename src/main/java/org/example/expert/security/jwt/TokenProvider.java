package org.example.expert.security.jwt;

import java.security.Key;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import javax.swing.*;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TokenProvider implements InitializingBean {

	//단순히 설정파일에서 받아온 암호화된 문자열
	private final String secret;

	//만료시간
	private final Long validityInMilliseconds;

	//실제로 JWT 서명에 사용될 키
	private Key key;

	//"Bearer " 까지 되어있는 prefix 설정 스트링
	private final String bearerPrefix;

	/**
	* Spring 빈으로 등록된 클래스의 생성자에 value 가 붙어있으면 자동 주입
	 * final 필드로 선언할 수 있음
	*/
	public TokenProvider(@Value("${jwt.secret}") String secret,
		@Value("${jwt.expiration-in-ms}") Long validityInMilliseconds,
		@Value("${jwt.bearer-prefix}") String bearerPrefix) {
		this.secret = secret;
		this.validityInMilliseconds = validityInMilliseconds;
		this.bearerPrefix = bearerPrefix;
	}

	//빈 초기화 완료 후 작업할 초기화 코드
	@Override
	public void afterPropertiesSet() throws Exception {
		//시크릿 키의 길이 확인용 변수
		byte[] decodedKey = Base64.getDecoder().decode(secret);
		if (decodedKey.length < 32) {
			throw new IllegalArgumentException("JWT 시크릿 키는 32바이트 이상이여야 합니다.");
		}

		//문자열을 Base64 디코딩 후 key 에 저장
		this.key = Keys.hmacShaKeyFor(decodedKey);
	}

	//토큰 생성 메서드
	public String createToken(Authentication authentication) {
		String authorities = authentication.getAuthorities().stream()
			.map(GrantedAuthority::getAuthority)
			.collect(Collectors.joining(","));

		long now = (new Date()).getTime();
		Date expiryDate = new Date(now + validityInMilliseconds);

		return Jwts.builder()
			.setSubject(authentication.getName())
			.claim("auth", authorities)
			.setExpiration(expiryDate)
			.signWith(key, SignatureAlgorithm.HS512)
			.compact();
	}

	//토큰에서 Authentication 객체 추출
	public Authentication getAuthentication(String token) {
		Claims claims = Jwts
			.parserBuilder()
			.setSigningKey(key)
			.build()
			.parseClaimsJws(token)
			.getBody();

		Collection<? extends GrantedAuthority> authorities =
			Arrays.stream(claims.get("auth").toString().split(","))
			.map(SimpleGrantedAuthority::new)
			.toList();

		UserDetails principal = new User(claims.getSubject(), "", authorities);

		return new UsernamePasswordAuthenticationToken(principal, token, authorities);
	}

	//토큰 검증 예외 던지기
	public void validateToken(String token) {
				Jwts.parserBuilder()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(token);
	}

	//요청 헤더 Authorization 에서 토큰 추출
	public String resolveToken(HttpServletRequest request) {
		//Bearer 이 붙은 토큰 값
		String bearerToken = request.getHeader("Authorization");

		//"Bearer " 까지 잘라서 뒤에 토큰값만 반환
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(bearerPrefix.length());
		}

		//조건에 맞지않을 시 null 반환
		return null;
	}

}
