package com.meuatelieweb.backend.domain.orderitem;

import com.meuatelieweb.backend.domain.order.Order;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "order_items")
@Entity(name = "OrderItem")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="type", discriminatorType = DiscriminatorType.STRING)
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String title;
    private String description;
    private Double cost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_order")
    private Order order;

    private LocalDateTime createdAt;

    private LocalDateTime dueDate;

    private LocalDateTime deliveredAt;

    @Builder.Default
    private Boolean isActive = true;
}
