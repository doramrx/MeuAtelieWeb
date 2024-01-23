package com.meuatelieweb.backend.domain.order;

import com.meuatelieweb.backend.domain.customer.Customer;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class OrderSpecification {

    private static Specification<Order> filterByOrderNumber(Integer orderNumber) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("orderNumber"), orderNumber);
    }

    private static Specification<Order> filterByCreatedAt(LocalDateTime createdAt) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("createdAt"), createdAt);
    }

    private static Specification<Order> filterByFinishedAt(LocalDateTime finishedAt) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("finishedAt"), finishedAt);
    }

    private static Specification<Order> filterByCustomerName(String customerName) {
        return (root, query, criteriaBuilder) -> {
            Join<Order, Customer> customerJoin = root.join("customer");
            return criteriaBuilder.like(criteriaBuilder.lower(
                    customerJoin.get("name")), "%" + customerName.toLowerCase() + "%"
            );
        };
    }

    private static Specification<Order> filterByCustomerEmail(String customerEmail) {
        return (root, query, criteriaBuilder) -> {
            Join<Order, Customer> customerJoin = root.join("customer");
            return criteriaBuilder.equal(customerJoin.get("email"), customerEmail);
        };
    }

    private static Specification<Order> filterByIsActive(Boolean isActive) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("isActive"), isActive);
    }

    public static Specification<Order> applyFilter(
            Integer orderNumber,
            LocalDateTime createdAt,
            LocalDateTime finishedAt,
            String customerName,
            String customerEmail,
            Boolean isActive)
    {
        Specification<Order> specification = Specification.where(null);

        if (orderNumber != null && orderNumber > 0) {
            specification = specification.and(filterByOrderNumber(orderNumber));
        }

        if (createdAt != null && !createdAt.equals(LocalDateTime.MIN)) {
            specification = specification.and(filterByCreatedAt(createdAt));
        }

        if (finishedAt != null && !finishedAt.equals(LocalDateTime.MIN)) {
            specification = specification.and(filterByFinishedAt(finishedAt));
        }

        if (customerName != null && !customerName.isBlank()) {
            specification = specification.and(filterByCustomerName(customerName));
        }

        if (customerEmail != null && !customerEmail.isBlank()) {
            specification = specification.and(filterByCustomerEmail(customerEmail));
        }

        if (isActive != null) {
            specification = specification.and(filterByIsActive(isActive));
        }

        return specification;
    }
}
