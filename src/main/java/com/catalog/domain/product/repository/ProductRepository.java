package com.catalog.domain.product.repository;

import com.catalog.domain.product.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("""
                SELECT  p
                FROM Product p
                LEFT JOIN p.labels l
            
                WHERE (
                    :category IS NULL
                    OR EXISTS (
                        SELECT cl
                        FROM p.labels cl
                        WHERE cl.type = 'CATEGORY'
                        AND cl.name = :category
                    )
                )
            
                GROUP BY p
            
                ORDER BY
                    SUM(
                        CASE
                            WHEN l.name IN :labelNames THEN l.score
                            ELSE 0
                        END
                    ) DESC
            """)
    Set<Product> findTopScoringProductsByLabelNames(
            @Param("labelNames") Set<String> labelNames,
            @Param("category") String category
    );


    @Query("""
                SELECT l.name
                FROM Product p
                JOIN p.labels l
                WHERE l.type = 'CATEGORY'
                  AND p.merchant.id = :merchantId
                GROUP BY l.name
                ORDER BY SUM(l.score) DESC
            """)
    List<String> findTopCategoriesByMerchant(
            @Param("merchantId") Long merchantId,
            Pageable pageable
    );

    @Query("""
    SELECT p
    FROM Product p
    JOIN p.labels l
    WHERE l.type = 'CATEGORY'
      AND l.name = :category
      AND p.merchant.id = :merchantId
    ORDER BY l.score DESC
""")
    List<Product> findTopProductByCategory(
            @Param("merchantId") Long merchantId,
            @Param("category") String category,
            Pageable pageable
    );

    Page<Product> findByMerchantIdOrderByCreatedAtDesc(Long merchantId, Pageable pageable);

}
