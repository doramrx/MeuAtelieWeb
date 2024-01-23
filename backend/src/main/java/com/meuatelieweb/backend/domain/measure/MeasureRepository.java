package com.meuatelieweb.backend.domain.measure;

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
public interface MeasureRepository extends JpaRepository<Measure, UUID> {

    Optional<Measure> findByIdAndIsActiveTrue(UUID id);

    Page<Measure> findAll(Specification<Measure> specification, Pageable pageable);

    boolean existsByIdAndIsActiveTrue(UUID id);

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, UUID id);

    @Query("""
            UPDATE Measure
            SET isActive = false
            WHERE id = :id
            """)
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    void inactivateMeasureById(UUID id);

    Optional<Measure> findByNameAndIsActiveTrue(String name);
}
