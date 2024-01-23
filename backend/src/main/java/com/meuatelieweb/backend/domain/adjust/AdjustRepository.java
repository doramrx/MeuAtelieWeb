package com.meuatelieweb.backend.domain.adjust;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface AdjustRepository extends JpaRepository<Adjust, UUID> {

    Optional<Adjust> findByIdAndIsActiveTrue(UUID id);

    Page<Adjust> findAll(Specification<Adjust> specification, Pageable pageable);

    Set<Adjust> findByIdInAndIsActiveTrue(Set<UUID> ids);

    boolean existsByIdAndIsActiveTrue(UUID id);

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, UUID id);

    @Query("""
            UPDATE Adjust
            SET isActive = false
            WHERE id = :id
            """)
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    void inactivateAdjustById(UUID id);

    Optional<Adjust> findByNameAndIsActiveTrue(String name);
}
