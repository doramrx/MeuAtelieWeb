package com.meuatelieweb.backend.domain.adjust;

import org.springframework.data.jpa.domain.Specification;

public class AdjustSpecification {

    private static Specification<Adjust> filterByName(String name) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(
                criteriaBuilder.lower(root.get("name")),
                "%" + name.toLowerCase() + "%"
        );
    }

    private static Specification<Adjust> filterByIsActive(Boolean isActive) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("isActive"), isActive);
    }

    public static Specification<Adjust> applyFilter(String name, Boolean isActive) {
        Specification<Adjust> specification = Specification.where(null);

        if (name != null && !name.isBlank()) {
            specification = specification.and(filterByName(name));
        }

        if (isActive != null) {
            specification = specification.and(filterByIsActive(isActive));
        }

        return specification;
    }
}
