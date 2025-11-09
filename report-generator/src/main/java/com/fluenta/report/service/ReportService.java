package com.fluenta.report.service;

import com.fluenta.report.client.ChecklistServiceClient;
import com.fluenta.report.model.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportService {
    private final ChecklistServiceClient checklistClient;

    public ReportService(ChecklistServiceClient checklistClient) {
        this.checklistClient = checklistClient;
    }

    public ComplianceReport generateComplianceReport(String checklistId) {
        Checklist checklist = checklistClient.getChecklist(checklistId);

        if (checklist == null) {
            throw new RuntimeException("Checklist not found: " + checklistId);
        }

        List<ChecklistItem> items = checklist.getItems();

        // Calculate overall status
        long completed = items.stream().filter(i -> "COMPLETED".equals(i.getStatus())).count();
        long partial = items.stream().filter(i -> "PARTIAL".equals(i.getStatus())).count();
        long pending = items.stream().filter(i -> "PENDING".equals(i.getStatus())).count();

        ComplianceReport.OverallStatus overallStatus = ComplianceReport.OverallStatus.builder()
                .totalItems(items.size())
                .completedItems((int) completed)
                .partialItems((int) partial)
                .pendingItems((int) pending)
                .completionPercentage(items.size() > 0 ? (completed * 100.0 / items.size()) : 0.0)
                .build();

        // Calculate category summaries
        Map<String, List<ChecklistItem>> itemsByCategory = items.stream()
                .collect(Collectors.groupingBy(ChecklistItem::getCategory));

        Map<String, ComplianceReport.CategorySummary> categorySummaries = new HashMap<>();
        itemsByCategory.forEach((category, categoryItems) -> {
            long categoryCompleted = categoryItems.stream()
                    .filter(i -> "COMPLETED".equals(i.getStatus()))
                    .count();

            categorySummaries.put(category, ComplianceReport.CategorySummary.builder()
                    .category(category)
                    .total(categoryItems.size())
                    .completed((int) categoryCompleted)
                    .percentage(categoryItems.size() > 0 ? (categoryCompleted * 100.0 / categoryItems.size()) : 0.0)
                    .build());
        });

        // Categorize requirements
        List<String> completedReqs = items.stream()
                .filter(i -> "COMPLETED".equals(i.getStatus()))
                .map(i -> i.getId() + ": " + i.getRequirement())
                .collect(Collectors.toList());

        List<String> partialReqs = items.stream()
                .filter(i -> "PARTIAL".equals(i.getStatus()))
                .map(i -> i.getId() + ": " + i.getRequirement())
                .collect(Collectors.toList());

        List<String> pendingReqs = items.stream()
                .filter(i -> "PENDING".equals(i.getStatus()))
                .map(i -> i.getId() + ": " + i.getRequirement())
                .collect(Collectors.toList());

        return ComplianceReport.builder()
                .checklistId(checklistId)
                .checklistName(checklist.getName())
                .generatedAt(Instant.now().toString())
                .overallStatus(overallStatus)
                .categorySummaries(categorySummaries)
                .completedRequirements(completedReqs)
                .partialRequirements(partialReqs)
                .pendingRequirements(pendingReqs)
                .build();
    }

    public GapReport generateGapReport(String checklistId) {
        Checklist checklist = checklistClient.getChecklist(checklistId);

        if (checklist == null) {
            throw new RuntimeException("Checklist not found: " + checklistId);
        }

        List<GapReport.Gap> gaps = checklist.getItems().stream()
                .filter(item -> !"COMPLETED".equals(item.getStatus()))
                .map(item -> GapReport.Gap.builder()
                        .requirementId(item.getId())
                        .requirement(item.getRequirement())
                        .category(item.getCategory())
                        .status(item.getStatus())
                        .reason(item.getEvidence().isEmpty() ? "No evidence provided" : "Incomplete evidence")
                        .build())
                .collect(Collectors.toList());

        // Identify critical gaps (pending items in critical categories)
        List<String> criticalGaps = gaps.stream()
                .filter(gap -> "PENDING".equals(gap.getStatus()))
                .filter(gap -> Arrays.asList("Access Control", "Data Protection").contains(gap.getCategory()))
                .map(gap -> gap.getRequirementId() + ": " + gap.getRequirement())
                .collect(Collectors.toList());

        // Generate basic recommendations
        List<String> recommendations = new ArrayList<>();
        if (!gaps.isEmpty()) {
            recommendations.add("Upload evidence documents for pending requirements");
            recommendations.add("Review and complete partially covered requirements");
            recommendations.add("Focus on critical gaps in Access Control and Data Protection first");
        }

        return GapReport.builder()
                .checklistId(checklistId)
                .generatedAt(Instant.now().toString())
                .gaps(gaps)
                .criticalGaps(criticalGaps)
                .recommendations(recommendations)
                .build();
    }

    public SuggestionResponse generateSuggestions(SuggestionRequest request) {
        // In a real implementation, this would call an AI service
        // For POC, we'll generate rule-based suggestions

        List<SuggestionResponse.Suggestion> suggestions = request.getGaps().stream()
                .map(gap -> {
                    String priority = gap.contains("password") || gap.contains("access") ? "HIGH" : "MEDIUM";
                    List<String> actionItems = Arrays.asList(
                            "Create or locate the required documentation",
                            "Upload the document for AI analysis",
                            "Review and address any gaps identified by the AI"
                    );

                    return SuggestionResponse.Suggestion.builder()
                            .gap(gap)
                            .recommendation("Prepare documentation addressing: " + gap)
                            .priority(priority)
                            .actionItems(actionItems)
                            .build();
                })
                .collect(Collectors.toList());

        return SuggestionResponse.builder()
                .suggestions(suggestions)
                .generatedAt(Instant.now().toString())
                .build();
    }
}
