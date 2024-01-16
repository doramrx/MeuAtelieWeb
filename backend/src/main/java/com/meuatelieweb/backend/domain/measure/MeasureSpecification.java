package com.meuatelieweb.backend.domain.measure;

import org.springframework.data.jpa.domain.Specification;

public class MeasureSpecification {

    private static Specification<Measure> filterByName(String name) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(
                criteriaBuilder.lower(root.get("name")),
                "%" + name.toLowerCase() + "%"
        );
    }

    private static Specification<Measure> filterByIsActive(Boolean isActive) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("isActive"), isActive);
    }

    public static Specification<Measure> applyFilter(String name, Boolean isActive) {
        Specification<Measure> specification = Specification.where(null);

        if (name != null && !name.isBlank()) {
            specification = specification.and(filterByName(name));
        }

        if (isActive != null) {
            specification = specification.and(filterByIsActive(isActive));
        }

        return specification;
    }
}
