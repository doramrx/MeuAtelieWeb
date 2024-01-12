package com.meuatelieweb.backend.domain.customer;

import org.springframework.data.jpa.domain.Specification;

public class CustomerSpecification {

    private static Specification<Customer> filterByName(String name) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(
                criteriaBuilder.lower(root.get("name")),
                "%" + name.toLowerCase() + "%"
        );
    }

    private static Specification<Customer> filterByEmail(String email) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("email"), email);
    }

    private static Specification<Customer> filterByPhone(String phone) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("phone"), phone);
    }

    private static Specification<Customer> filterByIsActive(Boolean isActive) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("isActive"), isActive);
    }

    public static Specification<Customer> applyFilter(String name, String email, String phone, Boolean isActive) {
        Specification<Customer> specification = Specification.where(null);

        if (name != null && !name.isBlank()) {
            specification = specification.and(filterByName(name));
        }

        if (email != null && !email.isBlank()) {
            specification = specification.and(filterByEmail(email));
        }

        if (phone != null && !phone.isBlank()) {
            specification = specification.and(filterByPhone(phone));
        }

        if (isActive != null) {
            specification = specification.and(filterByIsActive(isActive));
        }

        return specification;
    }
}
