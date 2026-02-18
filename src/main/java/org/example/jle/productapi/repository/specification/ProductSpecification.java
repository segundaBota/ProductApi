package org.example.jle.productapi.repository.specification;

import jakarta.persistence.criteria.Predicate;
import org.example.jle.productapi.repository.entity.ProductEntity;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProductSpecification {

    public static Specification<ProductEntity> filterBy(Map<String, String> filters) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            filters.forEach((key, value) -> {
                if (value != null && !value.isEmpty()) {
                    switch (key) {
                        case "name":
                        case "description":
                            predicates.add(criteriaBuilder.like(
                                    criteriaBuilder.lower(root.get(key)), 
                                    "%" + value.toLowerCase() + "%"));
                            break;
                        case "price":
                            predicates.add(criteriaBuilder.equal(root.get(key), Double.valueOf(value)));
                            break;
                    }
                }
            });

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}