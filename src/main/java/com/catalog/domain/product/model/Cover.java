package com.catalog.domain.product.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cover {
    private String imageUrl;
    private String title;
    private String description;
    private String logoUrl;
}
