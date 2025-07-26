package com.catalog.domain.product.service;

import com.catalog.domain.product.component.LabelGenerator;
import com.catalog.domain.product.model.Label;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class LabellingServiceImpl implements LabellingService {

    private final LabelGenerator generator;

    public static final double USER_LABEL_SCORE = 0.1;

    private final Set<String> validLabelNames = new HashSet<>();

    //todo: populate these with labels we keep getting
    private final HashMap<String, Integer> labelToReview = new HashMap<>();

    public LabellingServiceImpl(LabelGenerator generator) {
        this.generator = generator;
        generator.getPossibleLabels().forEach((key, value) -> validLabelNames.addAll(value));
    }

    @Override
    public Set<Label> label(String imageUrl, Set<String> userLabelsName) {
        userLabelsName.retainAll(validLabelNames);
        Set<Label> generatedLabels = generator.generate(imageUrl);

        generatedLabels.stream()
                .map(Label::getName)
                .map(userLabelsName::remove);

        Set<Label> userLabels = userLabelsName.stream()
                .map(name -> new Label(name, USER_LABEL_SCORE))
                .collect(Collectors.toSet());

        generatedLabels.addAll(userLabels);
        return generatedLabels;
    }

    @Override
    public Set<String> extractValidLabels(String searchString) {
        Set<String> validMatches = new HashSet<>();
        String[] words = searchString.toLowerCase().split("[\\W_]+");

        // Check individual words
        for (String word : words) {
            if (validLabelNames.contains(word)) {
                validMatches.add(word);
            } else {
                labelToReview.merge(word, 1, Integer::sum);
            }
        }

        // Check adjacent word pairs (bigrams)
        for (int i = 0; i < words.length - 1; i++) {
            String pair = words[i] + Character.toUpperCase(words[i + 1].charAt(0)) + words[i + 1].substring(1);

            if (validLabelNames.contains(pair)) {
                validMatches.add(pair);
            }
        }

        return validMatches;
    }


}
