package com.catalog.domain.product.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "labels")
public class Product {
    @Id
    @GeneratedValue
    private Long id;

    @Lob
    private String imageUrl;

    private String description;

    private BigDecimal price;

    @Transient
    private String productCategory;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Label> labels = new HashSet<>();

    private LocalDateTime createdAt = LocalDateTime.now();

    @ElementCollection
    private List<Integer> size;

    @ManyToOne
    private Merchant merchant;

    private Integer quantity;
}
