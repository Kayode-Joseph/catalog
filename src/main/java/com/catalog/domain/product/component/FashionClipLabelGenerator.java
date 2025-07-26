package com.catalog.domain.product.component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import com.catalog.domain.product.model.Label;

import java.util.*;

@Component
@Slf4j
public class FashionClipLabelGenerator implements LabelGenerator {

    private static final String PYTHON_API_URL = "http://localhost:5000/label";
    private static final String PYTHON_LABELS_URL = "http://localhost:5000/labels";
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Set<Label> generate(String imageUrl) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("image_url", imageUrl);
            HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(PYTHON_API_URL, request, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List<FashionClipResponse> results = objectMapper.readValue(
                        response.getBody(),
                        new TypeReference<List<FashionClipResponse>>() {}
                );

                Set<Label> labels = new HashSet<>();
                for (FashionClipResponse r : results) {
                    labels.add(new Label(r.getLabel(), r.getScore()));
                }
                return labels;
            }
        } catch (Exception e) {
            log.error("Failed to fetch labels from Python service", e);
        }

        return Collections.emptySet();
    }

    public Map<String, Set<String>> getPossibleLabels() {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(PYTHON_LABELS_URL, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Set<String>> labelsMap = objectMapper.readValue(
                        response.getBody(),
                        new TypeReference<Map<String, Set<String>>>() {}
                );
                return labelsMap;
            }
        } catch (Exception e) {
            log.error("Failed to fetch possible labels from Python service", e);
            throw new RuntimeException(e);
        }

        return Collections.emptyMap();
    }

    @Data
    private static class FashionClipResponse {
        private String label;
        private double score;
    }
}
