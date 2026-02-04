package com.catalog.domain.product.service;

import com.catalog.domain.product.model.Label;
import com.catalog.domain.product.model.Product;
import com.catalog.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
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

    public Set<Product> searchProduct(String searchString, String category) {
        Set<String> labelNames = labellingService.extractValidLabels(searchString);
        return repository.findTopScoringProductsByLabelNames(labelNames, category);
    }

    public Set<Product> getTopMerchantCategories(Long merchantId, int limit) {
        List<String> categories = repository.findTopCategoriesByMerchant(merchantId, PageRequest.of(0, limit));
        return getTopProductPerCategory(merchantId, categories, PageRequest.of(0, 1));
    }

    public Set<Product> getTopProductPerCategory(Long merchantId, List<String> categories, Pageable pageable) {
        Set<Product> result = new HashSet<>();
        categories.forEach((category) -> {
            List<Product> topProducts = repository.findTopProductByCategory(merchantId, category, pageable);
            Set<Product> products = topProducts.stream().peek((product) -> product.setProductCategory(category)).collect(Collectors.toSet());
            result.addAll(products);
        });
        return result;
    }


    public Page<Product> getProductsByMerchant(Long merchantId, Pageable pageable) {
        return repository.findByMerchantIdOrderByCreatedAtDesc(merchantId, pageable);
    }
}
