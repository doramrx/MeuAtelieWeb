package com.meuatelieweb.backend.domain.customermeasure;

import com.meuatelieweb.backend.domain.measure.Measure;
import com.meuatelieweb.backend.domain.orderitem.OrderItem;
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
@Table(name = "customer_measurements")
@Entity(name = "CustomerMeasure")
public class CustomerMeasure {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_measurement", nullable = false)
    private Measure measure;

    private Double measurementValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_order_item", nullable = false)
    private OrderItem orderItem;

    @Builder.Default
    private Boolean isActive = true;
}
