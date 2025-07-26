package com.catalog.domain.product.traffic.dto;

import com.catalog.domain.product.model.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private String imageUrl;
    private String description;
    private Category category;
}
