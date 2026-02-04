package com.catalog.domain.product.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductLabel {
    private Label brand;
    private Label category;
    private Set<Label> labels;
}
