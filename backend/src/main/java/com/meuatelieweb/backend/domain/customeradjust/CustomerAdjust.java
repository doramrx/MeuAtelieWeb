package com.meuatelieweb.backend.domain.customeradjust;

import com.meuatelieweb.backend.domain.adjust.Adjust;
import com.meuatelieweb.backend.domain.orderitem.OrderItem;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "customer_adjustments")
@Entity(name = "CustomerAdjust")
public class CustomerAdjust {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private Double adjustmentCost;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_adjustment", nullable = false)
    private Adjust adjust;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_order_item", nullable = false)
    private OrderItem orderItem;

    @Builder.Default
    private Boolean isActive = true;
}