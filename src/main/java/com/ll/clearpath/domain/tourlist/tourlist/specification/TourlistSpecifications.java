package com.ll.clearpath.domain.tourlist.tourlist.specification;

import com.ll.clearpath.domain.tourlist.tourlist.entity.Tourlist;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class TourlistSpecifications {

    public static Specification<Tourlist> searchByCategoryAndKeywords(String category, List<String> searches) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (category.equals("all")) {
                if (searches.isEmpty()) {
                    predicates.add(criteriaBuilder.conjunction());
                } else {
                    List<Predicate> searchPredicates = new ArrayList<>();
                    for (String search : searches) {
                        searchPredicates.add(criteriaBuilder.like(root.get("title"), "%" + search + "%"));
                        Join<Object, Object> keywordJoin = root.join("keywordLists").join("keyword");
                        searchPredicates.add(criteriaBuilder.like(keywordJoin.get("content"), "%" + search + "%"));
                    }
                    predicates.add(criteriaBuilder.or(searchPredicates.toArray(new Predicate[0])));
                }
            } else if (category.equals("title")) {
                if (searches.isEmpty()) {
                    predicates.add(criteriaBuilder.conjunction());
                } else {
                    for (String search : searches) {
                        predicates.add(criteriaBuilder.like(root.get("title"), "%" + search + "%"));
                    }
                }
            } else if (category.equals("keyword")) {
                if (searches.isEmpty()) {
                    predicates.add(criteriaBuilder.conjunction());
                } else {
                    for (String search : searches) {
                        Join<Object, Object> keywordJoin = root.join("keywordLists").join("keyword");
                        predicates.add(criteriaBuilder.like(keywordJoin.get("content"), "%" + search + "%"));
                    }
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
