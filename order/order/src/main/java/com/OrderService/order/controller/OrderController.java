package com.OrderService.order.controller;


import com.OrderService.order.entity.Order;
import com.OrderService.order.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        // Verify user exists
        ResponseEntity<?> userResponse = restTemplate.getForEntity(
                "http://user-service:8081/api/users/" + order.getUserId(), Object.class);
        if (!userResponse.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(orderRepository.save(order));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable Long id) {
        return orderRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable Long id, @RequestBody Order orderDetails) {
        return orderRepository.findById(id)
                .map(order -> {
                    order.setUserId(orderDetails.getUserId());
                    order.setProductId(orderDetails.getProductId());
                    order.setQuantity(orderDetails.getQuantity());
                    return ResponseEntity.ok(orderRepository.save(order));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteOrder(@PathVariable Long id) {
        return orderRepository.findById(id)
                .map(order -> {
                    orderRepository.delete(order);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}