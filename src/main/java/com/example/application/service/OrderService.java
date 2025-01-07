package com.example.application.service;

import com.example.application.model.Order;
import com.example.application.model.OrderDetails;
import com.example.application.repository.OrderDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderDetailsRepository orderDetailsRepository;

    public List<OrderDetails> getOrderDetailsByOrder(Order order) {
        return orderDetailsRepository.findByOrder(order);
    }
}
