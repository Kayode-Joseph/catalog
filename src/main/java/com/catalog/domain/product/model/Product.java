package com.catalog.domain.product.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
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

    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private Category category;

    private String description;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Label> labels = new HashSet<>();

    private LocalDateTime createdAt = LocalDateTime.now();
}
