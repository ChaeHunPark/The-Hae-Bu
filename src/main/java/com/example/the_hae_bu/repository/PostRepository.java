package com.example.the_hae_bu.repository;

import com.example.the_hae_bu.domain.Post;
import com.example.the_hae_bu.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {


}
