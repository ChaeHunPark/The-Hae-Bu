package com.example.the_hae_bu;

import com.example.the_hae_bu.domain.User;
import com.example.the_hae_bu.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional // 영속성 콘텍스트 유지
public class _07_PersistenceContextTest {
    @PersistenceContext
    EntityManager em;

    @Autowired
    UserRepository userRepository;

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
}
