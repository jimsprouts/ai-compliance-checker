package com.fluenta.checklist.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Evidence {
    private String documentId;
    private String documentName;
    private Double confidence;
    private Instant uploadedAt;
    private String relevantSections;
}
