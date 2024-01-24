package com.meuatelieweb.backend.domain.customeradjust;

import com.meuatelieweb.backend.domain.adjust.Adjust;
import com.meuatelieweb.backend.domain.adjust.AdjustService;
import com.meuatelieweb.backend.domain.customeradjust.dto.SaveCustomerAdjustDTO;
import com.meuatelieweb.backend.domain.orderitem.OrderItem;
import jakarta.persistence.EntityNotFoundException;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CustomerAdjustService {

    @Autowired
    private CustomerAdjustRepository repository;

    @Autowired
    private AdjustService adjustService;

    @Transactional
    public List<CustomerAdjust> addCustomerAdjusts(
            @NonNull
            OrderItem item,
            @NonNull
            Set<SaveCustomerAdjustDTO> saveCustomerAdjusts
    ) {

        Set<Adjust> adjusts = adjustService.getAdjusts(
                saveCustomerAdjusts.stream()
                        .map(SaveCustomerAdjustDTO::getAdjustmentId)
                        .collect(Collectors.toSet())
        );

        List<CustomerAdjust> customerAdjusts = adjusts.stream().map(adjust ->
                CustomerAdjust.builder()
                        .adjustmentCost(adjust.getCost())
                        .adjust(adjust)
                        .orderItem(item)
                        .build()
        ).toList();

        return repository.saveAllAndFlush(customerAdjusts);
    }

    @Transactional
    public void deleteCustomerAdjusts(@NonNull Set<UUID> ids) {
        if (!repository.existsByIdIn(ids)) {
            throw new EntityNotFoundException("Some of the given customer adjusts do not exist");
        }
        repository.inactivateCustomerAdjustById(ids);
    }
}
