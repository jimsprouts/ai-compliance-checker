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
public class GapAnalysisRequest {
    private List<RequirementItem> requirements;
    private List<EvidenceItem> evidenceList;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RequirementItem {
        private String id;
        private String requirement;
        private String status;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EvidenceItem {
        private String documentName;
        private String requirement;
    }
}
