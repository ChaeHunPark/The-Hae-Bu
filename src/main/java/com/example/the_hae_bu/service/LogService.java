package com.example.the_hae_bu.service;

import com.example.the_hae_bu.domain.Log;
import com.example.the_hae_bu.repository.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LogService {
    @Autowired private LogRepository logRepository;

    // 부모와 별개로 자신만의 트랜잭션을 새로 만든다.
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void 로그_저장(String message) {
        logRepository.save(new Log(message));
    }

}
