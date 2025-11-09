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
public class GapReport {
    private String checklistId;
    private String generatedAt;
    private List<Gap> gaps;
    private List<String> criticalGaps;
    private List<String> recommendations;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Gap {
        private String requirementId;
        private String requirement;
        private String category;
        private String status;
        private String reason;
    }
}
