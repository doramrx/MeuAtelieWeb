package com.meuatelieweb.backend.domain.customeradjust;

import com.meuatelieweb.backend.domain.adjust.Adjust;
import com.meuatelieweb.backend.domain.adjust.AdjustService;
import com.meuatelieweb.backend.domain.customeradjust.dto.SaveCustomerAdjustDTO;
import com.meuatelieweb.backend.domain.orderitem.OrderItem;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomerAdjustService {

    @Autowired
    private CustomerAdjustRepository repository;

    @Autowired
    private AdjustService adjustService;

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

        //return repository.saveAllAndFlush(customerAdjusts);
    }


/*    @Transactional
    public CustomerAdjust addCustomerAdjust(
            @NonNull
            SaveCustomerAdjustDTO saveCustomerAdjustDTO,
            @NonNull
            OrderItem orderItem
    ) {

        Adjust adjust = adjustService.findByNameAndIsActiveTrue(saveCustomerAdjustDTO.getAdjustmentName());

        CustomerAdjust customerAdjust = CustomerAdjust.builder()
                .adjust(adjust)
                .adjustmentCost(saveCustomerAdjustDTO.getAdjustmentCost())
                .orderItem(orderItem)
                .build();

        return repository.save(customerAdjust);
    }*/
}
