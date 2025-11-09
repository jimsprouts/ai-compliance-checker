package com.fluenta.checklist.service;

import com.fluenta.checklist.model.*;
import com.fluenta.checklist.repository.ChecklistRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChecklistService {
    private final ChecklistRepository repository;

    public ChecklistService(ChecklistRepository repository) {
        this.repository = repository;
    }

    public List<Checklist> getAllChecklists() {
        return repository.findAll();
    }

    public Optional<Checklist> getChecklistById(String id) {
        return repository.findById(id);
    }

    public Optional<ChecklistItem> updateItemStatus(String checklistId, String itemId, StatusUpdateRequest request) {
        Optional<Checklist> checklistOpt = repository.findById(checklistId);
        if (checklistOpt.isEmpty()) {
            return Optional.empty();
        }

        Checklist checklist = checklistOpt.get();
        Optional<ChecklistItem> itemOpt = checklist.getItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst();

        if (itemOpt.isEmpty()) {
            return Optional.empty();
        }

        ChecklistItem item = itemOpt.get();

        // Add new evidence first
        if (request.getEvidence() != null) {
            item.getEvidence().add(request.getEvidence());
        }

        // Determine status based on BEST (highest confidence) evidence
        // This ensures that once good evidence is uploaded, status doesn't degrade
        ChecklistItem.ItemStatus bestStatus = determineBestStatus(item.getEvidence());
        item.setStatus(bestStatus);

        repository.save(checklist);
        return Optional.of(item);
    }

    /**
     * Determines the best status based on all evidence.
     * Priority: COMPLETED > PARTIAL > PENDING
     * Logic:
     * - If ANY evidence has confidence > 0.7, status is COMPLETED
     * - Else if ANY evidence has confidence > 0.3, status is PARTIAL
     * - Otherwise status is PENDING
     */
    private ChecklistItem.ItemStatus determineBestStatus(List<Evidence> evidenceList) {
        if (evidenceList.isEmpty()) {
            return ChecklistItem.ItemStatus.PENDING;
        }

        double maxConfidence = evidenceList.stream()
                .mapToDouble(Evidence::getConfidence)
                .max()
                .orElse(0.0);

        if (maxConfidence > 0.7) {
            return ChecklistItem.ItemStatus.COMPLETED;
        } else if (maxConfidence > 0.3) {
            return ChecklistItem.ItemStatus.PARTIAL;
        } else {
            return ChecklistItem.ItemStatus.PENDING;
        }
    }

    public Optional<ProgressResponse> getProgress(String checklistId) {
        Optional<Checklist> checklistOpt = repository.findById(checklistId);
        if (checklistOpt.isEmpty()) {
            return Optional.empty();
        }

        Checklist checklist = checklistOpt.get();
        List<ChecklistItem> items = checklist.getItems();

        int total = items.size();
        long completed = items.stream()
                .filter(item -> item.getStatus() == ChecklistItem.ItemStatus.COMPLETED)
                .count();
        long partial = items.stream()
                .filter(item -> item.getStatus() == ChecklistItem.ItemStatus.PARTIAL)
                .count();
        long pending = items.stream()
                .filter(item -> item.getStatus() == ChecklistItem.ItemStatus.PENDING)
                .count();

        double completionPercentage = total > 0 ? (completed * 100.0 / total) : 0.0;

        return Optional.of(ProgressResponse.builder()
                .checklistId(checklistId)
                .totalItems(total)
                .completedItems((int) completed)
                .partialItems((int) partial)
                .pendingItems((int) pending)
                .completionPercentage(Math.round(completionPercentage * 100.0) / 100.0)
                .build());
    }
}
