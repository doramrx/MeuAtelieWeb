package com.meuatelieweb.backend.domain.order;

import com.meuatelieweb.backend.domain.customer.Customer;
import com.meuatelieweb.backend.domain.orderitem.OrderItem;
import jakarta.annotation.Generated;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "orders")
@Entity(name = "Order")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private Integer orderNumber;

    private LocalDateTime dueDate;

    private LocalDateTime createdAt;

    private LocalDateTime deliveredAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_customer", nullable = false)
    private Customer customer;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_order")
    private List<OrderItem> orderItems;

    @Builder.Default
    private Boolean isActive = true;
}
