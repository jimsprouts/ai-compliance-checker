package com.fluenta.report.service;

import com.fluenta.report.client.ChecklistServiceClient;
import com.fluenta.report.client.EvidenceAnalyzerClient;
import com.fluenta.report.model.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportService {
    private final ChecklistServiceClient checklistClient;
    private final EvidenceAnalyzerClient evidenceAnalyzerClient;

    public ReportService(ChecklistServiceClient checklistClient, EvidenceAnalyzerClient evidenceAnalyzerClient) {
        this.checklistClient = checklistClient;
        this.evidenceAnalyzerClient = evidenceAnalyzerClient;
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

        // Get AI-powered recommendations and critical gaps from Evidence Analyzer
        List<String> criticalGaps = new ArrayList<>();
        List<String> recommendations = new ArrayList<>();
        if (!gaps.isEmpty()) {
            try {
                // Build request for Evidence Analyzer
                List<GapAnalysisRequest.RequirementItem> requirementItems = checklist.getItems().stream()
                        .map(item -> GapAnalysisRequest.RequirementItem.builder()
                                .id(item.getId())
                                .requirement(item.getRequirement())
                                .status(item.getStatus())
                                .build())
                        .collect(Collectors.toList());

                List<GapAnalysisRequest.EvidenceItem> evidenceItems = checklist.getItems().stream()
                        .filter(item -> !item.getEvidence().isEmpty())
                        .flatMap(item -> item.getEvidence().stream()
                                .map(evidence -> GapAnalysisRequest.EvidenceItem.builder()
                                        .documentName(evidence.getDocumentName())
                                        .requirement(item.getRequirement())
                                        .build()))
                        .collect(Collectors.toList());

                GapAnalysisRequest gapRequest = GapAnalysisRequest.builder()
                        .requirements(requirementItems)
                        .evidenceList(evidenceItems)
                        .build();

                // Call Evidence Analyzer for AI suggestions and critical gaps
                GapAnalysisResponse aiResponse = evidenceAnalyzerClient.analyzeGaps(gapRequest);

                if (aiResponse != null) {
                    // Use AI-provided suggestions
                    if (aiResponse.getSuggestions() != null && !aiResponse.getSuggestions().isEmpty()) {
                        recommendations = aiResponse.getSuggestions();
                    } else {
                        // Fallback to generic recommendations
                        recommendations.add("Upload evidence documents for pending requirements");
                        recommendations.add("Review and complete partially covered requirements");
                    }

                    // Use AI-provided critical gaps, supplemented with critical categories
                    if (aiResponse.getCriticalGaps() != null && !aiResponse.getCriticalGaps().isEmpty()) {
                        criticalGaps = new ArrayList<>(aiResponse.getCriticalGaps());
                    }

                    // Ensure all critical categories are represented (AI sometimes misses some)
                    List<String> criticalCategories = Arrays.asList("Access Control", "Data Protection", "Risk Management");
                    for (GapReport.Gap gap : gaps) {
                        if ("PENDING".equals(gap.getStatus()) &&
                                criticalCategories.contains(gap.getCategory())) {
                            String gapString = gap.getRequirementId() + ": " + gap.getRequirement();
                            if (!criticalGaps.contains(gapString)) {
                                criticalGaps.add(gapString);
                            }
                        }
                    }
                } else {
                    // Fallback to generic recommendations
                    recommendations.add("Upload evidence documents for pending requirements");
                    recommendations.add("Review and complete partially covered requirements");
                }
            } catch (Exception e) {
                System.err.println("Failed to get AI recommendations: " + e.getMessage());
                // Fallback to generic recommendations
                recommendations.add("Upload evidence documents for pending requirements");
                recommendations.add("Review and complete partially covered requirements");
            }
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
