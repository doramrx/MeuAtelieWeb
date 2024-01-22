package com.meuatelieweb.backend.domain.orderItem;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Table(name = "customer_adjustments")
@Entity(name = "CustomerAdjustments")
@PrimaryKeyJoinColumn(name = "id_order_item")
public class CustomerAdjustments extends OrderItem {
    private Double adjustmentCost;
}
