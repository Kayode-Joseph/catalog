package com.catalog.domain.product.repository;

import com.catalog.domain.product.model.Category;
import com.catalog.domain.product.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("""
            SELECT p
            FROM Product p
            JOIN p.labels l
            WHERE (:category IS NULL OR p.category = :category)
            AND l.name IN :labelNames
            GROUP BY p
            ORDER BY SUM(l.score) DESC
            """)
    Set<Product> findTopScoringProductsByLabelNames(
            @Param("labelNames") Set<String> labelNames,
            @Param("category") Category category
    );

}
