package com.meuatelieweb.backend.domain.orderItem;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "order_items")
@Entity(name = "OrderItem")
@Inheritance(strategy = InheritanceType.JOINED)
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String title;
    private String description;
    private Boolean isActive;
}
