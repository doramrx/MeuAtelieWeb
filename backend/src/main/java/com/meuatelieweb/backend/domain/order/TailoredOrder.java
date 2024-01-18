package com.meuatelieweb.backend.domain.order;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("TAILORED")
public class TailoredOrder extends Order {
    private String title;
    private String description;
    private Double cost;
}
