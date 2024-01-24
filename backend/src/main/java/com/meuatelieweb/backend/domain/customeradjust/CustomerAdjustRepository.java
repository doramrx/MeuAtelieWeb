package com.meuatelieweb.backend.domain.customeradjust;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.UUID;

@Repository
public interface CustomerAdjustRepository extends JpaRepository<CustomerAdjust, UUID> {

    boolean existsByIdInAndIsActiveTrue(Set<UUID> ids);

    boolean existsByIdIn(Set<UUID> ids);

    @Query("""
            UPDATE CustomerAdjust
            SET isActive = false
            WHERE id IN :ids
            """)
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    void inactivateCustomerAdjustById(Set<UUID> ids);
}
