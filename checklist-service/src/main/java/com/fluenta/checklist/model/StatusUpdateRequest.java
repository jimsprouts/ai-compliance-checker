package com.fluenta.checklist.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusUpdateRequest {
    private ChecklistItem.ItemStatus status;
    private Evidence evidence;
}
