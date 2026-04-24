package com.example.the_hae_bu.service;

import com.example.the_hae_bu.domain.Order;
import com.example.the_hae_bu.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {
    @Autowired private OrderRepository orderRepository;
    @Autowired private LogService logService;

    @Transactional
    public void 주문_진행 (String orderName) {
        orderRepository.save(new Order(orderName)); // 1. 주문 저장
        logService.로그_저장(orderName + " 로그");

        throw new RuntimeException("주문 처리 중 갑작스러운 서버 다운");
    }
}
