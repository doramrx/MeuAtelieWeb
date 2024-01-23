package com.meuatelieweb.backend.domain.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    Page<Order> findAll(Specification<Order> specification, Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT nextval('orders_order_number_seq')")
    Integer getNextOrderNumber();
}
