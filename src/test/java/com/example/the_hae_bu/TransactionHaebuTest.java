package com.example.the_hae_bu;

import com.example.the_hae_bu.domain.User;
import com.example.the_hae_bu.repository.UserRepository;
import com.example.the_hae_bu.service.HaebuService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
public class TransactionHaebuTest {

    @Autowired HaebuService haebuService;
    @Autowired UserRepository userRepository;


    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void 실험1_Transactional이_없으면_롤백이_안될까() {

        // 1. 유저 미리 생성 (이건 별도 트랜잭션으로 확실히 저장)
        haebuService.유저_만들기("해부5", 10000);

        // 2. 실험 실행
        try {
            haebuService.택배_시키기("해부5", 5000);
        } catch (Exception e) {
            System.out.println("에러 발생: " + e.getMessage());
        }

        // 3. 최종 결과 확인
        User user = userRepository.findByName("해부5");
        System.out.println("최종 DB의 돈: " + user.돈_확인하기());
    }

    @Test
    void 실험2_readOnly_true는_디비에_반영될까() {

        haebuService.유저_만들기("해부11", 10000);
        haebuService.돈_채우기("해부11",5000);

        User user = userRepository.findByName("해부11");

        System.out.println(user.돈_확인하기());
    }

}
