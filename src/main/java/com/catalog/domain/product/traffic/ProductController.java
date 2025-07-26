package com.catalog.domain.product.traffic;

import com.catalog.domain.product.model.Category;
import com.catalog.domain.product.model.Product;
import com.catalog.domain.product.service.ProductService;
import com.catalog.domain.product.traffic.dto.ProductDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

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
        product.setCategory(dto.getCategory());

        productService.saveProduct(product);
        return ResponseEntity.ok("Product saved successfully");
    }

    @GetMapping("/search")
    public ResponseEntity<Set<Product>> searchProducts(
            @RequestParam String query,
            @RequestParam(required = false) Category category
    ) {
        Set<Product> products = productService.searchProduct(query, category);
        return ResponseEntity.ok(products);
    }
}
