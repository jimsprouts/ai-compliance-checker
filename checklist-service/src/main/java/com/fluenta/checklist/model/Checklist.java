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
public class Checklist {
    private String id;
    private String name;
    private String description;

    @Builder.Default
    private List<ChecklistItem> items = new ArrayList<>();
}
