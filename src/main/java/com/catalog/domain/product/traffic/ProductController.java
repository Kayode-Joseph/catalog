package com.catalog.domain.product.traffic;

import com.catalog.domain.product.model.Category;
import com.catalog.domain.product.model.Product;
import com.catalog.domain.product.service.ProductService;
import com.catalog.domain.product.traffic.dto.ProductDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<String> createProduct(@RequestBody ProductDTO dto) {
        Product product = new Product();
        product.setImageUrl(dto.getImageUrl());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());

        productService.saveProduct(product);
        return ResponseEntity.ok("Product saved successfully");
    }

    @GetMapping("/search")
    public ResponseEntity<Set<Product>> searchProducts(
            @RequestParam String query,
            @RequestParam(required = false) String category
    ) {
        Set<Product> products = productService.searchProduct(query, category);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/top-categories")
    public ResponseEntity<List<ProductDTO>> getTopCategories(
            @RequestParam Long merchantId,
            @RequestParam(defaultValue = "4") int limit
    ) {
        Set<Product> topProducts = productService.getTopMerchantCategories(merchantId, limit);

        List<ProductDTO> response = topProducts.stream()
                .map(ProductDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/top-category-products")
    public ResponseEntity<Set<ProductDTO>> getTopProductsByCategory(
            @RequestParam Long merchantId,
            @RequestParam String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        Set<Product> topProducts = productService.getTopProductPerCategory(
                merchantId,
                Collections.singletonList(category),
                pageable
        );

        Set<ProductDTO> content = topProducts.stream()
                .map(ProductDTO::new)
                .collect(Collectors.toSet());

        return ResponseEntity.ok(content);
    }

    @GetMapping("/by-merchant")
    public Set<ProductDTO> getProductsByMerchant(
            @RequestParam Long merchantId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return productService.getProductsByMerchant(merchantId, pageable).getContent().stream()
                .map(ProductDTO::new).collect(Collectors.toSet());
    }

}
