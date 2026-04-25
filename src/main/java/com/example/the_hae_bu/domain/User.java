package com.example.the_hae_bu.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int money;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Post> posts = new ArrayList<>();

    public User(String name, int money) {
        this.money = money;
        this.name = name;
    }

    public void 돈_보내기(int money) {
        this.money -= money;
    }

    public int 돈_확인하기() {
        return this.money;
    }

    public void 돈_채우기(int money) {
        this.money += money;
    }


}
