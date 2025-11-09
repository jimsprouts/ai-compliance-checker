package com.fluenta.report.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Evidence {
    private String documentId;
    private String documentName;
    private Double confidence;
    private String uploadedAt;
    private String relevantSections;
}
