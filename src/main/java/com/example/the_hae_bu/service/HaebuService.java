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


    // 실험 1
    // 포인트 : @Transactional 어노테이션이 없다면?
    @Transactional
    public void 택배_시키기 (String name ,int 보낼돈) {
        User user = userRepository.findByName(name);
        user.돈_보내기(보낼돈);

        userRepository.save(user);

        System.out.println("현재 내돈 : " + user.돈_확인하기());
        System.out.println("--- 에러 발생 직전 ---");
        throw new RuntimeException("시스템 폭파됨..");

        // 뒤에는 물건 받는 로직이 있음
    }

    // 실험 2
    // 포인트 : readOnly = true 옵션을 붙인다면?
    @Transactional(readOnly = true)
    public void 서비스_돈_채우기(String name, int 돈) {
        User user = userRepository.findByName(name);
        user.돈_채우기(돈);
    }


    // 실험 3
    // 포인트 : 트랜잭셔널 어노테이션이 있는 메서드를 트랜잭셔널 어노테이션이 없는 메서드에 넣는다면?
    @Transactional
    public void 돈_채우기_트랜잭션_있음(String name, int 돈) {
        User user = userRepository.findByName(name);
        user.돈_채우기(돈);
    }

    public void 돈_채우기_트랜잭션_없음(String name, int 돈) {
        User user = userRepository.findByName(name);
        돈_채우기_트랜잭션_있음(name, 돈);
    }

    // 실험 4
    // 포인트 : Unchecked Exception (RuntimeException)과 Checked Exception (Exception)
    @Transactional
    public void 언체크_예외_발생(String name) {
        유저_만들기("해부1",1000);
        throw new RuntimeException("이건 롤백 되나?");
    }

    /*
    * 체크 예외는 롤백이 되지 않는다.
    * 비즈니스적으로 발생 할 수 있는 예외이니 개발자가 직접 처리해라! 라는 의미
    * 복구가 가능한 상황일 수도 있으니까 스프링도 함부로 롤백하지 않고 개발자에게 맡긴다.
    * @Transactional(rollbackFor = Exception.class)
    * 옵션을 추가해서 "Exception이 나도 롤백해라"라고 명령할 수 있음
    * */

    @Transactional
    public void 체크_예외_발생(String name) throws Exception {
        유저_만들기("해부2", 1000);
        throw new Exception("이건 롤백 안된다던데?");
    }




}
