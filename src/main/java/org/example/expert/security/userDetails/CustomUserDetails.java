package org.example.expert.security.userDetails;

import java.util.Collection;
import java.util.List;

import org.example.expert.domain.user.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails implements UserDetails {

	private final User user;

	public CustomUserDetails(User user) {
		this.user = user;
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getEmail();
	}

	//시큐리티가 사용할 권한 확인용 문자열 반환
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority("ROLE_" + user.getUserRole().name()));
	}

	//이하 시큐리티에서 유저 상태를 체크하는 로직 !인증!
	//계정이 만료되지 않았는가?
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	//계정이 잠겨있지 않은가?
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	//비밀번호가 만료되지 않았는가?
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	//계정이 활성화 상태인가?
	@Override
	public boolean isEnabled() {
		return true;
	}

}
