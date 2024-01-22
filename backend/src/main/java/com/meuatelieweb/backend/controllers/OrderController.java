package com.meuatelieweb.backend.controllers;

import com.meuatelieweb.backend.domain.order.OrderConverter;
import com.meuatelieweb.backend.domain.order.OrderService;
import com.meuatelieweb.backend.domain.order.dto.OrderDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("orders")
public class OrderController {
    @Autowired
    private OrderService service;

    @Autowired
    private OrderConverter converter;

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> findById(@PathVariable UUID id) {
        OrderDTO orderDTO = this.converter.toOrderDTO(service.findById(id));

        return ResponseEntity.ok().body(orderDTO);
    }
}
