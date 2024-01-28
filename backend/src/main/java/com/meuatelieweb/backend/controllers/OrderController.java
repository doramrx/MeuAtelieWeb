package com.meuatelieweb.backend.controllers;

import com.meuatelieweb.backend.domain.customeradjust.dto.SaveCustomerAdjustListDTO;
import com.meuatelieweb.backend.domain.customermeasure.dto.SaveCustomerMeasureListDTO;
import com.meuatelieweb.backend.domain.customermeasure.dto.UpdateCustomerMeasureDTO;
import com.meuatelieweb.backend.domain.order.OrderConverter;
import com.meuatelieweb.backend.domain.order.OrderService;
import com.meuatelieweb.backend.domain.order.dto.ListOrderDTO;
import com.meuatelieweb.backend.domain.order.dto.OrderDTO;
import com.meuatelieweb.backend.domain.order.dto.SaveOrderDTO;
import com.meuatelieweb.backend.domain.order.dto.UpdateOrderDTO;
import com.meuatelieweb.backend.domain.orderitem.dto.SaveOrderItemDTO;
import com.meuatelieweb.backend.domain.orderitem.dto.UpdateOrderItemDTO;
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

    @PostMapping("/{orderId}/items/")
    public ResponseEntity<OrderDTO> addOrderItemToOrder (
            @PathVariable UUID orderId,
            @RequestBody SaveOrderItemDTO saveOrderItem
    ) {
        OrderDTO savedOrder = this.converter.toOrderDTO(service.addOrderItemToOrder(orderId, saveOrderItem));

        return ResponseEntity.ok().body(savedOrder);
    }

    @PostMapping("/{orderId}/items/{itemId}/adjusts")
    public ResponseEntity<OrderDTO> addAdjustsToOrderItem (
            @PathVariable UUID orderId,
            @PathVariable UUID itemId,
            @RequestBody SaveCustomerAdjustListDTO saveCustomerAdjustList
    ) {
        OrderDTO orderDTO = this.converter.toOrderDTO(
                service.addAdjustsToOrderItem(orderId, itemId, saveCustomerAdjustList)
        );

        return ResponseEntity.ok().body(orderDTO);
    }

    @PostMapping("/{orderId}/items/{itemId}/measures")
    public ResponseEntity<OrderDTO> addMeasuresToOrderItem (
            @PathVariable UUID orderId,
            @PathVariable UUID itemId,
            @RequestBody SaveCustomerMeasureListDTO saveCustomerMeasureListDTO
    ) {
        OrderDTO orderDTO = this.converter.toOrderDTO(
                service.addMeasuresToOrderItem(orderId, itemId, saveCustomerMeasureListDTO)
        );

        return ResponseEntity.ok().body(orderDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderDTO> updateOrder(
            @PathVariable
            UUID id,
            @RequestBody
            @Valid
            UpdateOrderDTO updateOrderDTO)
    {
        OrderDTO updatedOrder = converter.toOrderDTO(service.updateOrder(id, updateOrderDTO));

        return ResponseEntity.ok().body(updatedOrder);
    }

    @PutMapping("/{orderId}/items/{itemId}")
    public ResponseEntity<OrderDTO> updateOrderItem(
            @PathVariable
            UUID orderId,
            @PathVariable
            UUID itemId,
            @RequestBody
            @Valid
            UpdateOrderItemDTO updateOrderItemDTO)
    {
        OrderDTO updatedOrder = converter.toOrderDTO(service.updateOrderItem(orderId, itemId, updateOrderItemDTO));

        return ResponseEntity.ok().body(updatedOrder);
    }

    @PutMapping("/{orderId}/items/{itemId}/customer-measures/{customerMeasureId}")
    public ResponseEntity<OrderDTO> updateOrderItemMeasures(
            @PathVariable
            UUID orderId,
            @PathVariable
            UUID itemId,
            @PathVariable
            UUID customerMeasureId,
            @RequestBody
            @Valid
            UpdateCustomerMeasureDTO updateCustomerMeasureDTO
    ) {
        OrderDTO updatedOrder = converter.toOrderDTO(
                service.updateOrderItemCustomerMeasure(orderId, itemId, customerMeasureId, updateCustomerMeasureDTO)
        );

        return ResponseEntity.ok().body(updatedOrder);
    }

    @PatchMapping("/{orderId}/items/{itemId}/deliver")
    public ResponseEntity<Void> deliverItem(@PathVariable UUID orderId, @PathVariable UUID itemId) {
        service.deliverItem(orderId, itemId);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable UUID id) {
        service.deleteOrder(id);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{orderId}/items/{itemId}")
    public ResponseEntity<Void> singleDeleteItemFromOrder(@PathVariable UUID orderId, @PathVariable UUID itemId) {
        service.singleDeleteItemFromOrder(orderId, itemId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{orderId}/items/{itemId}/adjusts/{customerAdjustId}")
    public ResponseEntity<OrderDTO> singleDeleteCustomerAdjustFromItem(
            @PathVariable UUID orderId,
            @PathVariable UUID itemId,
            @PathVariable UUID customerAdjustId) {

        OrderDTO orderDTO = converter.toOrderDTO(
                service.singleDeleteCustomerAdjustFromItem(orderId, itemId, customerAdjustId)
        );

        return ResponseEntity.ok().body(orderDTO);
    }

    @DeleteMapping("/{orderId}/items/{itemId}/measures/{customerMeasureId}")
    public ResponseEntity<OrderDTO> singleDeleteCustomerMeasureFromItem(
            @PathVariable UUID orderId,
            @PathVariable UUID itemId,
            @PathVariable UUID customerMeasureId) {

        OrderDTO orderDTO = converter.toOrderDTO(
                service.singleDeleteCustomerMeasureFromItem(orderId, itemId, customerMeasureId)
        );

        return ResponseEntity.ok().body(orderDTO);
    }
}
