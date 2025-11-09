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
public class ChecklistItem {
    private String id;
    private String category;
    private String requirement;
    private List<String> hints;
    private String status;
    private List<Evidence> evidence;
}
