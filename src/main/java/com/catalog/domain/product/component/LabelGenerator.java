package com.catalog.domain.product.component;

import com.catalog.domain.product.model.Label;

import java.util.Map;
import java.util.Set;

public interface LabelGenerator {
    Set<Label> generate(String imageUrl);
    Map<String, Set<String>> getPossibleLabels();
}
