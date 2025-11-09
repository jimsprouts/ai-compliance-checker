package com.fluenta.report.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplianceReport {
    private String checklistId;
    private String checklistName;
    private String generatedAt;
    private OverallStatus overallStatus;
    private Map<String, CategorySummary> categorySummaries;
    private List<String> completedRequirements;
    private List<String> pendingRequirements;
    private List<String> partialRequirements;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OverallStatus {
        private Integer totalItems;
        private Integer completedItems;
        private Integer partialItems;
        private Integer pendingItems;
        private Double completionPercentage;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategorySummary {
        private String category;
        private Integer total;
        private Integer completed;
        private Double percentage;
    }
}
