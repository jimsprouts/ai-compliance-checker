package com.fluenta.checklist.controller;

import com.fluenta.checklist.model.*;
import com.fluenta.checklist.service.ChecklistService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/checklists")
@CrossOrigin(origins = "*")
public class ChecklistController {
    private final ChecklistService service;

    public ChecklistController(ChecklistService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Checklist>> getAllChecklists() {
        return ResponseEntity.ok(service.getAllChecklists());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Checklist> getChecklistById(@PathVariable String id) {
        return service.getChecklistById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/items/{itemId}/status")
    public ResponseEntity<ChecklistItem> updateItemStatus(
            @PathVariable String id,
            @PathVariable String itemId,
            @RequestBody StatusUpdateRequest request) {
        return service.updateItemStatus(id, itemId, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/progress")
    public ResponseEntity<ProgressResponse> getProgress(@PathVariable String id) {
        return service.getProgress(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
