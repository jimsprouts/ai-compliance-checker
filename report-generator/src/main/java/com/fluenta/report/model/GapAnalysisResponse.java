package com.fluenta.report.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GapAnalysisResponse {
    private List<String> uncoveredRequirements;
    private List<PartiallyCoveredItem> partiallyCovered;
    private List<String> criticalGaps;
    private List<String> suggestions;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PartiallyCoveredItem {
        private String requirement;
        private String reason;
    }
}
