package com.meuatelieweb.backend.domain.customer;

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
interface CustomerRepository extends JpaRepository<Customer, UUID> {

    Optional<Customer> findByIdAndIsActiveTrue(UUID id);

    Page<Customer> findAll(Specification<Customer> specification, Pageable pageable);

    boolean existsByIdAndIsActiveTrue(UUID id);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    boolean existsByPhoneAndIdNot(String phone, UUID id);

    @Query("""
            UPDATE Customer
            SET isActive = false
            WHERE id = :id
            """)
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    void inactivateCustomerById(UUID id);

}
