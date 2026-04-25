package com.example.the_hae_bu;

import com.example.the_hae_bu.domain.Post;
import com.example.the_hae_bu.domain.User;
import com.example.the_hae_bu.repository.PostRepository;
import com.example.the_hae_bu.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Transactional // 테스트 완료 후 데이터 롤백을 위해 사용
public class NPlusOneTest_06 {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private EntityManager em; // 영속성 컨텍스트 초기화를 위해 필요

    @BeforeEach
    void setup() {
        // 1. 유저 3명 생성
        User user1 = userRepository.save(new User("해부학자1", 1000));
        User user2 = userRepository.save(new User("해부학자2", 2000));
        User user3 = userRepository.save(new User("해부학자3", 3000));

        // 2. 각 유저당 게시글 2개씩 생성
        postRepository.save(new Post("글1-1", user1));
        postRepository.save(new Post("글1-2", user1));

        postRepository.save(new Post("글2-1", user2));
        postRepository.save(new Post("글2-2", user2));

        postRepository.save(new Post("글3-1", user3));
        postRepository.save(new Post("글3-2", user3));

        // [중요] 영속성 컨텍스트 비우기
        // 이걸 안 하면 1차 캐시에 데이터가 남아서 쿼리가 안 나갈 수 있음
        em.flush();
        em.clear();
        System.out.println("--- 데이터 준비 완료 및 영속성 컨텍스트 초기화 ---");
    }

    @Test
    @DisplayName("N+1 문제를 눈으로 확인하는 테스트")
    void nPlusOneVisualTest() {
        System.out.println("=== 1. 모든 유저 조회 (쿼리 1번 예상) ===");
        List<User> users = userRepository.findAll();
        System.out.println("실제 조회된 유저 수: " + users.size());

        System.out.println("=== 2. 각 유저의 게시글에 접근 (추가 쿼리 N번 예상) ===");
        for (User user : users) {
            // 이 시점에 프록시 객체가 초기화되면서 DB에 쿼리를 날림
            System.out.println("유저명: " + user.getName() + " | 게시글 수: " + user.getPosts().size());
        }
    }

    @Test
    @DisplayName("Fetch Join으로 N+1 문제를 해결하는 테스트")
    void fetchJoinTest() {
        System.out.println("=== Fetch Join 실행 (쿼리 딱 1번 예상) ===");
        List<User> users = userRepository.findAllWithPostsFetchJoin();

        for (User user : users) {
            System.out.println("유저명: " + user.getName() + " | 게시글 수: " + user.getPosts().size());
        }
    }
}
