package com.example.the_hae_bu.repository;

import com.example.the_hae_bu.domain.Log;
import com.example.the_hae_bu.domain.Order;
import org.aspectj.weaver.ast.Or;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
