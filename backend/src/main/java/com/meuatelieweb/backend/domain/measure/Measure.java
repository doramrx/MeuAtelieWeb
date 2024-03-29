package com.meuatelieweb.backend.domain.measure;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "measurements")
@Entity(name = "Measure")
public class Measure {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "measurement")
    private String name;
    @Builder.Default
    private Boolean isActive = true;
}
