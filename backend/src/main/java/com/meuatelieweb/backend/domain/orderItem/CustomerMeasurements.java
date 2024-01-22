package com.meuatelieweb.backend.domain.orderItem;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Table(name = "customer_measurements")
@Entity(name = "CustomerMeasurements")
@PrimaryKeyJoinColumn(name = "id_order_item")
public class CustomerMeasurements extends OrderItem {
    private Double measurementValue;
}
