package org.example.expert.domain.auth.service;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@SpringBootTest
class AuthServiceTest {

	@Autowired
	private AuthService authService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private PasswordEncoder passwordEncoder;

	private static Random random = new Random();

	@Test
	@Transactional
	@Rollback(false)
	void signup_백만건의_유저가입() {
		int cnt = 1;
		String password = passwordEncoder.encode("1234");

		for(int i=0; i<100; i++) {
			for(int ii=0; ii<10000; ii++) {
				entityManager.persist(User.of("hong"+ cnt +"@gmail.com", password, nicknameGenerate(), UserRole.USER));
				cnt ++;
			}
			entityManager.flush();
			entityManager.clear();
		}
	}

	@Test
	@Transactional
	@Rollback(false)
	void signup_백만건의_유저가입_Jdbc() {
		String sql = "insert into users (email, password, nickname, user_role, created_at, modified_at) values (?, ?, ?, ?, ?, ?)";
		String password = passwordEncoder.encode("1234");
		List<User> users = new ArrayList<>();
		int cnt = 1;

		for(int i=0; i<100; i++) {
			for(int ii = 0; ii<10000; ii++) {
				users.add(User.of("hong" + cnt + "@gmail.com", password, nicknameGenerate(), UserRole.USER));
				cnt ++ ;
			}

			jdbcTemplate.batchUpdate(sql, users, users.size(),
				(ps, user) -> {
					ps.setString(1, user.getEmail());
					ps.setString(2, user.getPassword());
					ps.setString(3, user.getNickname());
					ps.setString(4, user.getUserRole().name());
					ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
					ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
				});

			users.clear();
		}
	}

	private static String nicknameGenerate() {

		List<String> lastNames = List.of("김","이","박","최","정","조","윤","장","임","차","제갈","남궁");
		List<String> firstNames = List.of("하늘", "지우", "도윤", "예린", "준호",
			"수아", "시우", "하린", "도현", "예빈",
			"현우", "서연", "지호", "유나", "은우",
			"하람", "민재", "다은", "서진", "연우",
			"태윤", "나연", "세진", "유진", "지안",
			"서우", "민석", "아린", "주원", "윤아",
			"성민", "다윤", "채원", "정우", "은채",
			"수진", "지성", "서현", "예준", "다현",
			"시윤", "하진", "민서", "태민", "하율",
			"서윤", "은서", "성우", "채윤", "수현");

		return lastNames.get(random.nextInt(lastNames.size())) + firstNames.get(random.nextInt(firstNames.size()));
	}
}