package com.meuatelieweb.backend.domain.customermeasure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface CustomerMeasureRepository extends JpaRepository<CustomerMeasure, UUID> {

    @Query("""
            SELECT cm
            FROM CustomerMeasure cm
            JOIN FETCH cm.orderItem
            WHERE cm.id = :id
            """)
    Optional<CustomerMeasure> findCustomerMeasureById(UUID id);

    boolean existsByIdIn(Set<UUID> ids);

    void deleteAllByIdIn(Set<UUID> ids);
}
