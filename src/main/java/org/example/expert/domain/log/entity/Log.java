package org.example.expert.domain.log.entity;

import org.example.expert.domain.common.entity.Timestamped;
import org.example.expert.domain.log.enums.LogStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "log")
public class Log extends Timestamped {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	//담당자가 등록될 게시물 ID
	private Long todoID;

	//담당자로 등록할 유저 ID
	private Long managerId;

	//성공, 실패의 상태 저장 필드 -> enum 으로 수정
	@Enumerated(EnumType.STRING)
	private LogStatus status;

	private Log(Long todoID, Long managerId, LogStatus logStatus) {
		this.todoID = todoID;
		this.managerId = managerId;
		this.status = logStatus;
	}

	//성공 스태틱메서드
	public static Log succeeded(Long todoID, Long managerId) {
		return new Log(todoID, managerId, LogStatus.SUCCESS);
	}

	//실패(예외) 스태틱메서드
	public static Log failed(Long todoID, Long managerId) {
		return new Log(todoID, managerId, LogStatus.FAILED);
	}
}
