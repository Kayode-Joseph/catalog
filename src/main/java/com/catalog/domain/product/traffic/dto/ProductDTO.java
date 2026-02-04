package com.catalog.domain.product.traffic.dto;

import com.catalog.domain.product.model.Category;
import com.catalog.domain.product.model.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {
    private String imageUrl;
    private String description;
    private BigDecimal price;
    private String productCategory;

    public ProductDTO(Product product) {
        this.imageUrl = product.getImageUrl();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.productCategory = product.getProductCategory(); // set transient category
    }
}
