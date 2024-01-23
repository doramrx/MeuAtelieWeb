package com.meuatelieweb.backend.domain.customeradjust;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CustomerAdjustRepository extends JpaRepository<CustomerAdjust, UUID> {
}
