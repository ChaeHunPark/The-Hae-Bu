package com.example.the_hae_bu.service;

import com.example.the_hae_bu.domain.User;
import com.example.the_hae_bu.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HaebuService {

    private final UserRepository userRepository;



    public void 유저_만들기 (String name, int money) {
        userRepository.save(new User(name, money));
    }

//    @Transactional     // 실험 포인트 : @Transactional 어노테이션이 없다면?
    public void 택배_시키기 (String name ,int 보낼돈) {
        User user = userRepository.findByName(name);
        user.돈_보내기(보낼돈);

        userRepository.save(user);

        System.out.println("현재 내돈 : " + user.돈_확인하기());
        System.out.println("--- 에러 발생 직전 ---");
        throw new RuntimeException("시스템 폭파됨..");

        // 뒤에는 물건 받는 로직이 있음


    }


}
