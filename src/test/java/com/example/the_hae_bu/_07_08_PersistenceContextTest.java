package com.example.the_hae_bu;

import com.example.the_hae_bu.domain.User;
import com.example.the_hae_bu.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional // 영속성 콘텍스트 유지
public class _07_08_PersistenceContextTest {
    @PersistenceContext
    EntityManager em;

    @Autowired
    UserRepository userRepository;

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    // 실험 7
    @Test
    @DisplayName("1차 캐시와 동일성 보장 실험")
    void persistenceTest() {
        // 유저  한 명 저장
        User user = new User("해부7", 7000);
        userRepository.save(user);

        // 영속성 컨텍스트를 DB 동기화하고 비우기
        em.flush();
        em.clear();

        System.out.println(" 1. 첫 번째 조회 (DB 쿼리 날아감");
        User firstUser = userRepository.findById(user.getId()).get();

        System.out.println(" 2. 두 번째 조회 (쿼리 안날아감)");
        User secondUser = userRepository.findById(user.getId()).get();

        System.out.println(" 3. 동일성 비교");
        System.out.println(" firstUser == secondUser : " + (firstUser == secondUser));

    }

    // 실험 8
    @Test
    @DisplayName("변경 감지(Dirty Checking) 실험")
    void dirtyCheckingTest() {
        // 영속성 컨텍스트 올리기
        User user = userRepository.save(new User("해부8",8000));

        // 영속성 컨텍스트 비우고 조회로 영속 상태 만들기
        em.flush();
        em.clear();
        User foundUser = userRepository.findById(user.getId()).get();

        System.out.println("1. 데이터 수정하기");
        foundUser.이름_수정("해부9");

        System.out.println("2. 트랜잭션 종료 직전 (flush 발생 예상)");
    }

}
