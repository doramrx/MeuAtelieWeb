package com.meuatelieweb.backend.controllers;

import com.meuatelieweb.backend.domain.order.OrderConverter;
import com.meuatelieweb.backend.domain.order.OrderService;
import com.meuatelieweb.backend.domain.order.dto.ListOrderDTO;
import com.meuatelieweb.backend.domain.order.dto.OrderDTO;
import com.meuatelieweb.backend.domain.order.dto.SaveOrderDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("orders")
public class OrderController {

    @Autowired
    private OrderService service;

    @Autowired
    private OrderConverter converter;

    @GetMapping
    public ResponseEntity<Page<ListOrderDTO>> findAll(
            Pageable pageable,
            @RequestParam(name = "orderNumber", required = false)
            Integer orderNumber,
            @RequestParam(name = "createdAt", required = false)
            LocalDateTime createdAt,
            @RequestParam(name = "finishedAt", required = false)
            LocalDateTime finishedAt,
            @RequestParam(name = "customerName", required = false)
            String customerName,
            @RequestParam(name = "customerEmail", required = false)
            String customerEmail,
            @RequestParam(name = "isActive", required = false)
            Boolean isActive
    ) {
        Page<ListOrderDTO> orderDTOPage = service.findAll(
                pageable, orderNumber, createdAt, finishedAt, customerName, customerEmail, isActive
        ).map(converter::toListOrderDTO);

        return ResponseEntity.ok().body(orderDTOPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> findById(@PathVariable UUID id) {
        OrderDTO orderDTO = this.converter.toOrderDTO(service.findById(id));

        return ResponseEntity.ok().body(orderDTO);
    }

    @PostMapping
    public ResponseEntity<OrderDTO> addOrder(
            @RequestBody @Valid SaveOrderDTO saveOrderDTO,
            UriComponentsBuilder uriComponentsBuilder
    ) {
        OrderDTO savedOrder = this.converter.toOrderDTO(service.addOrder(saveOrderDTO));

        URI uri = uriComponentsBuilder
                .path("/orders/{id}")
                .buildAndExpand(savedOrder.getId())
                .toUri();

        return ResponseEntity.created(uri).body(savedOrder);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable UUID id) {
        service.deleteOrder(id);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{orderId}/items/{itemId}/deliver")
    public ResponseEntity<Void> deliverItem(@PathVariable UUID orderId, @PathVariable UUID itemId) {
        service.deliverItem(orderId, itemId);

        return ResponseEntity.ok().build();
    }
}
