package com.meuatelieweb.backend.domain.customermeasure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.UUID;

@Repository
public interface CustomerMeasureRepository extends JpaRepository<CustomerMeasure, UUID> {

    boolean existsByIdInAndIsActiveTrue(Set<UUID> ids);

    boolean existsByIdIn(Set<UUID> ids);

    @Query("""
            UPDATE CustomerMeasure
            SET isActive = false
            WHERE id IN :ids
            """)
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    void inactivateCustomerMeasureById(Set<UUID> ids);
}
