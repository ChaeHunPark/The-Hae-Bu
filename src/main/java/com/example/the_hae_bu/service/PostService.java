package com.example.the_hae_bu.service;

import com.example.the_hae_bu.domain.Post;
import com.example.the_hae_bu.domain.User;
import com.example.the_hae_bu.repository.PostRepository;
import com.example.the_hae_bu.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PostService {
    @Autowired
    PostRepository postRepository;
    @Autowired
    UserRepository userRepository;


    // 실험 6
    // N+1을 직접 일으키고 로그 확인, Fetch join으로 다시 로그 확인
    @Transactional(readOnly = true)
    public void N플러스1_현상_재현() {
        System.out.println(" 모든 유저 조회 시작");
        List<User> users = userRepository.findAll();

        for(User user : users) {
            System.out.println("유저: " + user.getName() + "게시글 수: " + user.getPosts().size());
        }
    }
}
