package com.meuatelieweb.backend.domain.order;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.util.List;

@Entity
@DiscriminatorValue("ADJUST")
public class AdjustOrder extends Order {

    private List<Piece> pieces;

}
