package com.meuatelieweb.backend.domain.adjust;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "adjustments")
@Entity(name = "Adjust")
public class Adjust {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    private Double cost;
    @Builder.Default
    private Boolean isActive = true;
}
