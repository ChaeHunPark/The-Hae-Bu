package com.example.the_hae_bu.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "orders") // order는 SQL 예약어일 수 있으므로 테이블명 지정
@Getter
@NoArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String product;

    public Order(String product) {
        this.product = product;
    }
}
