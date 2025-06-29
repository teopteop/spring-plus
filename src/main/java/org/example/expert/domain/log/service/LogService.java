package org.example.expert.domain.log.service;

import org.example.expert.domain.log.entity.Log;
import org.example.expert.domain.log.repository.LogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LogService {
	private final LogRepository logRepository;

	public void logManagerRegistration(Long todoId, Long managerId){
		logRepository.save(Log.succeeded(todoId, managerId));
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void logManagerFailure(Long todoId, Long managerId){
		logRepository.save(Log.failed(todoId, managerId));

	}

}
