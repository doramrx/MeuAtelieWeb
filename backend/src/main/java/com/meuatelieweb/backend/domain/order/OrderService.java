package com.meuatelieweb.backend.domain.order;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrderService {
    @Autowired
    private OrderRepository repository;

    public Order findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("The given order does not exist"));
    }
}
