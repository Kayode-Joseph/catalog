package com.catalog.domain.product.service;

import com.catalog.domain.product.model.Merchant;
import com.catalog.domain.product.repository.MerchantRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MerchantService {

    private final MerchantRepository repository;

    public Merchant getMerchantByEmail(String email) {
        return repository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Merchant not found with email: " + email));
    }
}
