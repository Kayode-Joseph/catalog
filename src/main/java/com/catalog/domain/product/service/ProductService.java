package com.catalog.domain.product.service;

import com.catalog.domain.product.model.Category;
import com.catalog.domain.product.model.Label;
import com.catalog.domain.product.model.Product;
import com.catalog.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {

    @Autowired
    private final LabellingService labellingService;

    @Autowired
    private final ProductRepository repository;

    public void saveProduct(Product product) {
        log.info("New Product to process: {}", product);
        Set<String> userLabels = Arrays.stream(product.getDescription().split("\\s+"))
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
        Set<Label> labels = labellingService.label(product.getImageUrl(), userLabels);
        labels.forEach(label -> label.setProduct(product));
        product.setLabels(labels);
        repository.save(product);
    }

    public Set<Product> searchProduct(String searchString, Category category) {
        Set<String> labelNames = labellingService.extractValidLabels(searchString);
        return repository.findTopScoringProductsByLabelNames(labelNames, category);
    }


}
