package com.meuatelieweb.backend.domain.customeradjust;

import com.meuatelieweb.backend.domain.customermeasure.CustomerMeasure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface CustomerAdjustRepository extends JpaRepository<CustomerAdjust, UUID> {

    @Query("""
            SELECT ca
            FROM CustomerAdjust ca
            JOIN FETCH ca.orderItem
            WHERE ca.id = :id
            """)
    Optional<CustomerAdjust> findCustomerAdjustById(UUID id);

    boolean existsByIdIn(Set<UUID> ids);

    void deleteAllByIdIn(Set<UUID> ids);
}
