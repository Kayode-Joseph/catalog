package com.catalog.domain.product.service;

import com.catalog.domain.product.model.Label;

import java.util.Set;

public interface LabellingService {

    Set<Label> label(String imageUrl, Set<String> userLabels);

    Set<String> extractValidLabels(String searchString);

}
