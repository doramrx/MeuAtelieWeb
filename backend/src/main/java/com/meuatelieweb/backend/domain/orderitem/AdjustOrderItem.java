package com.meuatelieweb.backend.domain.orderitem;

import com.meuatelieweb.backend.domain.customeradjust.CustomerAdjust;
import com.meuatelieweb.backend.domain.order.Order;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@DiscriminatorValue("ADJUST")
public class AdjustOrderItem extends OrderItem {

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "orderItem")
    private List<CustomerAdjust> customerAdjustments;
}
