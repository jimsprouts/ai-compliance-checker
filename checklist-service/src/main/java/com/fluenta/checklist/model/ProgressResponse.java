package com.fluenta.checklist.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgressResponse {
    private String checklistId;
    private Integer totalItems;
    private Integer completedItems;
    private Integer partialItems;
    private Integer pendingItems;
    private Double completionPercentage;
}
