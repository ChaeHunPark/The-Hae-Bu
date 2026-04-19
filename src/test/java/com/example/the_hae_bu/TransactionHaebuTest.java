package com.example.the_hae_bu;

import com.example.the_hae_bu.domain.User;
import com.example.the_hae_bu.repository.UserRepository;
import com.example.the_hae_bu.service.HaebuService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TransactionHaebuTest {

    @Autowired HaebuService haebuService;
    @Autowired UserRepository userRepository;

    @Test
    void 실험_Transactional이_없으면_롤백이_안될까() {
        // 1. 유저 미리 생성 (이건 별도 트랜잭션으로 확실히 저장)
        haebuService.유저_만들기("해부3", 10000);

        // 2. 실험 실행
        try {
            haebuService.택배_시키기("해부3", 5000);
        } catch (Exception e) {
            System.out.println("에러 발생: " + e.getMessage());
        }

        // 3. 최종 결과 확인
        User user = userRepository.findByName("해부3");
        System.out.println("최종 DB의 돈: " + user.돈_확인하기());
    }
}
