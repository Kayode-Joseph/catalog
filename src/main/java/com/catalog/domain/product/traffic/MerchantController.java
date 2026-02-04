package com.catalog.domain.product.traffic;

import com.catalog.domain.product.model.Merchant;
import com.catalog.domain.product.service.MerchantService;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/merchants")
@RequiredArgsConstructor
public class MerchantController {

    private final MerchantService service;

    @GetMapping("/by-email")
    public ResponseEntity<Merchant> getMerchantByEmail(@RequestParam String email) {
        Merchant merchant = service.getMerchantByEmail(email);
        return ResponseEntity.ok(merchant);
    }
}
