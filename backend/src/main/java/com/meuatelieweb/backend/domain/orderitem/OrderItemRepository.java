package com.meuatelieweb.backend.domain.orderitem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {

    Optional<OrderItem> findByIdAndIsActiveTrue(UUID id);

    boolean existsByIdIn(Set<UUID> ids);

    @Query("""
            UPDATE OrderItem
            SET isActive = false
            WHERE id IN :ids
            """)
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    void inactivateOrderItemById(Set<UUID> ids);
}
