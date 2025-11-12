package com.bvv.filegeneration.entity;

import com.bvv.filegeneration.common.enums.FieldType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "template_fields")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemplateField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", nullable = false)
    private Template template;

    @Column(nullable = false)
    private String fieldName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FieldType fieldType;

    @Column(nullable = false)
    private Boolean required;

    @Column(name = "min_length")
    private Integer minLength;

    @Column(name = "max_length")
    private Integer maxLength;

    @Column(name = "min_value")
    private java.math.BigDecimal minValue;

    @Column(name = "max_value")
    private java.math.BigDecimal maxValue;

    @Column(name = "date_format")
    private String dateFormat;

    @Column(name = "possible_values")
    private String possibleValues;

    @Column(name = "error_injection_strategy")
    private String errorInjectionStrategy;

    @Column(name = "field_position")
    private Integer position;
}

