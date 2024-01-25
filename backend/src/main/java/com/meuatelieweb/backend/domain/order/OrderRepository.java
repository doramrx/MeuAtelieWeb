package com.meuatelieweb.backend.domain.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    Page<Order> findAll(Specification<Order> specification, Pageable pageable);

    Optional<Order> findByIdAndIsActiveTrue(UUID id);

    boolean existsByIdAndFinishedAtNull(UUID id);

    @Query(nativeQuery = true, value = "SELECT nextval('orders_order_number_seq')")
    Integer getNextOrderNumber();

    @Query("""
            UPDATE Order
            SET isActive = false
            WHERE id = :id
            """)
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    void inactivateOrderById(UUID id);
}
