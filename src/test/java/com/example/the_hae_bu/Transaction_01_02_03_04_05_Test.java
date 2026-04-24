package com.example.the_hae_bu;

import com.example.the_hae_bu.domain.User;
import com.example.the_hae_bu.repository.LogRepository;
import com.example.the_hae_bu.repository.OrderRepository;
import com.example.the_hae_bu.repository.UserRepository;
import com.example.the_hae_bu.service.HaebuService;
import com.example.the_hae_bu.service.OrderService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class Transaction_01_02_03_04_05_Test {

    @Autowired HaebuService haebuService;
    @Autowired UserRepository userRepository;
    @Autowired OrderService orderService;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    LogRepository logRepository;


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
        haebuService.서비스_돈_채우기("해부11",5000);

        User user = userRepository.findByName("해부11");

        System.out.println(user.돈_확인하기());
    }

    @Test
    void 실험3_트랜잭션_없는_메서드에_트랜잭션_있는_메서드_넣으면_디비에_반영이_될까(){
        haebuService.유저_만들기("해부11", 10000);
        haebuService.돈_채우기_트랜잭션_없음("해부11",5000);
        User user = userRepository.findByName("해부11");
        System.out.println(user.돈_확인하기());
    }

    @Test
    void 실험4_체크_예외와_언_체크_예외의_롤백_테스트() {

        try {
            haebuService.언체크_예외_발생("언체크");
        } catch (Exception e) {
            System.out.println("에러 메시지: " + e.getMessage());
        }


        try {
            haebuService.체크_예외_발생("체크");
        } catch (Exception e) {
            System.out.println("에러 메시지: " + e.getMessage());
        }

        // 결과 확인
        System.out.println("--- DB 결과 확인 ---");
        userRepository.findAll().forEach(u -> System.out.println(u.getName()));
    }

    @Test
    void 실험5_전파_속성_테스트() {
        try {
            orderService.주문_진행("치킨");
        } catch (Exception e) {
            System.out.println("예외 : " + e.getMessage());
        }
        System.out.println("----- DB 결과 -----");
        System.out.println("주문 갯수 : " + orderRepository.count());
        System.out.println("로그 갯수 : " + logRepository.count());
    }

}
