package com.meuatelieweb.backend.domain.customermeasure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CustomerMeasureRepository extends JpaRepository<CustomerMeasure, UUID> {
}
