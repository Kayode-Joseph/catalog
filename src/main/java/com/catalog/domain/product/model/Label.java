package com.catalog.domain.product.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.ToStringExclude;

@Entity
@Data
@NoArgsConstructor
public class Label {
    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private Double score;

    @ManyToOne
    @ToStringExclude
    @JsonIgnore
    private Product product;

    @JsonIgnore
    @Enumerated(EnumType.STRING)
    private LabelType type;

    public Label(String name, Double score) {
        this.name = name;
        this.score = score;
    }
}
