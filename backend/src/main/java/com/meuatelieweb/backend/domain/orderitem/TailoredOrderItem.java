package com.meuatelieweb.backend.domain.orderitem;

import com.meuatelieweb.backend.domain.customermeasure.CustomerMeasure;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@DiscriminatorValue("TAILORED")
public class TailoredOrderItem extends OrderItem{

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "orderItem")
    private List<CustomerMeasure> customerMeasures;
}