package com.example.the_hae_bu.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int money;

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
