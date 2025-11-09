package com.fluenta.checklist.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChecklistItem {
    private String id;
    private String category;
    private String requirement;
    private List<String> hints;
    private ItemStatus status;

    @Builder.Default
    private List<Evidence> evidence = new ArrayList<>();

    public enum ItemStatus {
        PENDING,
        PARTIAL,
        COMPLETED
    }
}
