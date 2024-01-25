package com.meuatelieweb.backend.domain.orderitem;

import com.meuatelieweb.backend.domain.customeradjust.CustomerAdjust;
import com.meuatelieweb.backend.domain.customeradjust.CustomerAdjustService;
import com.meuatelieweb.backend.domain.customeradjust.dto.SaveCustomerAdjustDTO;
import com.meuatelieweb.backend.domain.customeradjust.dto.SaveCustomerAdjustListDTO;
import com.meuatelieweb.backend.domain.customermeasure.CustomerMeasure;
import com.meuatelieweb.backend.domain.customermeasure.CustomerMeasureService;
import com.meuatelieweb.backend.domain.customermeasure.dto.SaveCustomerMeasureDTO;
import com.meuatelieweb.backend.domain.customermeasure.dto.SaveCustomerMeasureListDTO;
import com.meuatelieweb.backend.domain.customermeasure.dto.UpdateCustomerMeasureDTO;
import com.meuatelieweb.backend.domain.order.Order;
import com.meuatelieweb.backend.domain.orderitem.dto.SaveOrderItemDTO;
import com.meuatelieweb.backend.domain.orderitem.dto.UpdateOrderItemDTO;
import jakarta.persistence.EntityNotFoundException;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderItemService {

    @Autowired
    private OrderItemRepository repository;

    @Autowired
    private CustomerAdjustService customerAdjustService;

    @Autowired
    private CustomerMeasureService customerMeasureService;

    public OrderItem findActiveOrderItemById(@NonNull UUID id) {
        return repository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("The given item does not exist or is already inactive"));
    }

    @Transactional
    public List<OrderItem> addOrderItems(
            @NonNull
            Order order,
            @NonNull
            Set<SaveOrderItemDTO> items
    ) {

        if (items.isEmpty()) {
            throw new IllegalArgumentException("The order must have at least one item");
        }

        List<OrderItem> orderItems = items.stream().map(item -> {
            this.validateItemType(item.getType());
            this.validateItemTitle(item.getTitle());
            this.validateItemDueDate(item.getDueDate());

            if (item.getType() == OrderType.ADJUST) {
                AdjustOrderItem adjustOrderItem = new AdjustOrderItem();
                adjustOrderItem.setTitle(item.getTitle());
                adjustOrderItem.setDescription(item.getDescription());
                adjustOrderItem.setOrder(order);
                adjustOrderItem.setCreatedAt(LocalDateTime.now());
                adjustOrderItem.setDueDate(item.getDueDate());

                AdjustOrderItem adjustItem = repository.saveAndFlush(adjustOrderItem);

                this.validateIfThereAreDuplicatedCustomerAdjusts(item.getAdjusts());

                List<CustomerAdjust> customerAdjusts = customerAdjustService.addCustomerAdjusts(
                        adjustItem, item.getAdjusts()
                );

                adjustItem.setCost(
                        customerAdjusts.stream().mapToDouble(CustomerAdjust::getAdjustmentCost).sum()
                );

                adjustItem.setCustomerAdjustments(customerAdjusts);

                return adjustItem;
            }

            this.validateTailoredItemCost(item.getCost());

            TailoredOrderItem tailoredOrderItem = new TailoredOrderItem();
            tailoredOrderItem.setTitle(item.getTitle());
            tailoredOrderItem.setDescription(item.getDescription());
            tailoredOrderItem.setCost(item.getCost());
            tailoredOrderItem.setOrder(order);
            tailoredOrderItem.setCreatedAt(LocalDateTime.now());
            tailoredOrderItem.setDueDate(item.getDueDate());

            TailoredOrderItem tailoredItem = repository.saveAndFlush(tailoredOrderItem);

            this.validateIfThereAreDuplicatedDuplicatedMeasures(item.getMeasures());

            List<CustomerMeasure> customerMeasures = customerMeasureService.addCustomerMeasures(
                    tailoredItem, item.getMeasures()
            );

            tailoredItem.setCustomerMeasures(customerMeasures);

            return tailoredItem;

        }).toList();

        return repository.saveAllAndFlush(orderItems);
    }

    @Transactional
    public void addAdjustsToOrderItem(
            @NonNull UUID itemId,
            @NonNull SaveCustomerAdjustListDTO adjusts
    ) {
        this.validateIfOrderItemWasDelivered(itemId);
        OrderItem item = findActiveOrderItemById(itemId);

        if (item instanceof TailoredOrderItem) {
            throw new IllegalArgumentException("The item type is invalid");
        }

        ((AdjustOrderItem) item).getCustomerAdjustments().forEach(customerAdjust -> {
            adjusts.getAdjusts().stream()
                    .filter(saveCustomerAdjustDTO -> saveCustomerAdjustDTO.getAdjustmentId().equals(customerAdjust.getAdjust().getId()))
                    .findFirst()
                    .ifPresent(e -> {
                        throw new IllegalArgumentException("Some of the adjusts are already being used in this item");
                    });
        });
        customerAdjustService.addCustomerAdjusts(item, adjusts.getAdjusts());
    }

    @Transactional
    public void addMeasuresToOrderItem(
            @NonNull UUID itemId,
            @NonNull SaveCustomerMeasureListDTO measures
    ) {
        this.validateIfOrderItemWasDelivered(itemId);
        OrderItem item = findActiveOrderItemById(itemId);

        if (item instanceof AdjustOrderItem) {
            throw new IllegalArgumentException("The item type is invalid");
        }

        ((TailoredOrderItem) item).getCustomerMeasures().forEach(customerMeasure -> {
            measures.getMeasures().stream()
                    .filter(saveCustomerMeasureDTO -> saveCustomerMeasureDTO.getMeasurementId().equals(customerMeasure.getMeasure().getId()))
                    .findFirst()
                    .ifPresent(e -> {
                        throw new IllegalArgumentException("Some of the measures are already being used in this item");
                    });
        });
        customerMeasureService.addCustomerMeasures(item, measures.getMeasures());
    }

    @Transactional
    public void updateOrderItem(
            @NonNull UUID id,
            @NonNull UpdateOrderItemDTO updateOrderItemDTO
    ) {
        this.validateIfOrderItemWasDelivered(id);

        OrderItem orderItem = findActiveOrderItemById(id);

        if (updateOrderItemDTO.getTitle() != null) {
            orderItem.setTitle(updateOrderItemDTO.getTitle());
        }

        if (updateOrderItemDTO.getDescription() != null) {
            orderItem.setDescription(updateOrderItemDTO.getDescription());
        }

        if (updateOrderItemDTO.getDueDate() != null) {
            this.validateItemDueDate(updateOrderItemDTO.getDueDate());
            orderItem.setDueDate(updateOrderItemDTO.getDueDate());
        }

        if (orderItem instanceof TailoredOrderItem) {

            if (updateOrderItemDTO.getCost() != null) {
                this.validateTailoredItemCost(updateOrderItemDTO.getCost());
                orderItem.setCost(updateOrderItemDTO.getCost());
            }
        }
        repository.save(orderItem);
    }

    @Transactional
    public void updateOrderItemCustomerMeasure(
            @NonNull UUID itemId,
            @NonNull UUID customerMeasureId,
            @NonNull UpdateCustomerMeasureDTO updateCustomerMeasureDTO
    ) {
        this.validateIfOrderItemExistsAndIsActive(itemId);
        this.validateIfOrderItemWasDelivered(itemId);

        customerMeasureService.updateCustomerMeasure(customerMeasureId, updateCustomerMeasureDTO);
    }

    @Transactional
    public void deliverItem(@NonNull UUID id) {

        this.validateIfOrderItemWasDelivered(id);

        OrderItem orderItem = repository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("The given item does not exist or is already inactive"));

        orderItem.setDeliveredAt(LocalDateTime.now());

        repository.save(orderItem);
    }

    @Transactional
    public void deleteOrderItems(@NonNull List<OrderItem> items) {

        Set<UUID> ids = items.stream()
                .map(OrderItem::getId)
                .collect(Collectors.toSet());

        if (!repository.existsByIdIn(ids)) {
            throw new EntityNotFoundException("Some of the given order items do not exist");
        }

        items.forEach(item -> {
            if (item instanceof AdjustOrderItem adjustOrderItem) {

                if (!adjustOrderItem.getCustomerAdjustments().isEmpty()) {
                    Set<UUID> customerAdjustsIds = adjustOrderItem.getCustomerAdjustments().stream()
                            .map(CustomerAdjust::getId)
                            .collect(Collectors.toSet());

                    customerAdjustService.deleteCustomerAdjusts(customerAdjustsIds);
                }
            }

            if (item instanceof TailoredOrderItem tailoredOrderItem) {

                if (!tailoredOrderItem.getCustomerMeasures().isEmpty()) {
                    Set<UUID> customerMeasuresIds = tailoredOrderItem.getCustomerMeasures().stream()
                            .map(CustomerMeasure::getId)
                            .collect(Collectors.toSet());

                    customerMeasureService.deleteCustomerMeasures(customerMeasuresIds);
                }
            }
        });

        repository.inactivateOrderItemById(ids);
    }

    @Transactional
    public void singleDeleteItemFromOrder(@NonNull OrderItem item) {

        this.validateItemDeliveryDate(item.getDeliveredAt());

        if (item instanceof AdjustOrderItem) {
            customerAdjustService.deleteCustomerAdjusts(
                    ((AdjustOrderItem) item).getCustomerAdjustments().stream()
                            .map(CustomerAdjust::getId)
                            .collect(Collectors.toSet())
            );

        } else if (item instanceof TailoredOrderItem) {
            customerMeasureService.deleteCustomerMeasures(
                    ((TailoredOrderItem) item).getCustomerMeasures().stream()
                            .map(CustomerMeasure::getId)
                            .collect(Collectors.toSet())
            );
        }
        repository.inactivateOrderItemById(Set.of(item.getId()));
    }

    @Transactional
    public void singleDeleteCustomerAdjustFromItem(@NonNull UUID itemId, @NonNull UUID customerAdjustId) {

        this.validateIfOrderItemExistsAndIsActive(itemId);
        this.validateIfOrderItemWasDelivered(itemId);

        customerAdjustService.singleDeleteCustomerAdjust(customerAdjustId);
    }

    @Transactional
    public void singleDeleteCustomerMeasureFromItem(@NonNull UUID itemId, @NonNull UUID customerMeasureId) {

        this.validateIfOrderItemExistsAndIsActive(itemId);
        this.validateIfOrderItemWasDelivered(itemId);

        customerMeasureService.singleDeleteCustomerMeasure(customerMeasureId);
    }

    private void validateItemType(OrderType type) {
        if (type == null) {
            throw new IllegalArgumentException("Item type cannot be null");
        }
        if (!type.equals(OrderType.ADJUST) && !type.equals(OrderType.TAILORED)) {
            throw new HttpMessageConversionException("Item type must be ADJUST or TAILORED");
        }
    }

    private void validateItemTitle(String title) {
        if (title == null) {
            throw new IllegalArgumentException("Item title cannot be null");
        }
    }

    private void validateItemDueDate(LocalDateTime dueDate) {
        if (dueDate == null) {
            throw new IllegalArgumentException("Item due date cannot be null");
        }
        if (dueDate.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Item due date is invalid");
        }
    }

    private void validateTailoredItemCost(Double cost) {
        if (cost == null) {
            throw new IllegalArgumentException("The given cost cannot be empty");
        }
        if (cost < 0.01) {
            throw new IllegalArgumentException("The given cost cannot be lesser than 0.01");
        }
    }

    private void validateItemDeliveryDate(LocalDateTime deliveredAt) {
        if (deliveredAt != null) {
            throw new IllegalArgumentException("The item cannot be modified because it has already been delivered");
        }
    }

    private void validateIfOrderItemExistsAndIsActive(@NonNull UUID id) {
        if (!repository.existsByIdAndIsActiveTrue(id)) {
            throw new EntityNotFoundException("The given item does not exist or is already inactive");
        }
    }

    private void validateIfOrderItemWasDelivered(@NonNull UUID id) {
        if (!repository.existsByIdAndDeliveredAtNull(id)) {
            throw new IllegalArgumentException("The item cannot be modified because it has already been delivered");
        }
    }

    private void validateIfThereAreDuplicatedCustomerAdjusts(List<SaveCustomerAdjustDTO> saveCustomerAdjusts) {
        long distinctCount = saveCustomerAdjusts.stream().distinct().count();
        if (saveCustomerAdjusts.size() != distinctCount) {
            throw new IllegalArgumentException("There are duplicated adjusts");
        }
    }

    private void validateIfThereAreDuplicatedDuplicatedMeasures(List<SaveCustomerMeasureDTO> saveCustomerMeasures) {
        List<UUID> measuresId = saveCustomerMeasures.stream()
                .map(SaveCustomerMeasureDTO::getMeasurementId)
                .toList();

        long distinctCount = measuresId.stream().distinct().count();
        if (measuresId.size() != distinctCount) {
            throw new IllegalArgumentException("There are duplicated measures");
        }
    }
}
