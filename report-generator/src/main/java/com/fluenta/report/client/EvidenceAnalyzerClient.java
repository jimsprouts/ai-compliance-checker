package com.fluenta.report.client;

import com.fluenta.report.model.GapAnalysisRequest;
import com.fluenta.report.model.GapAnalysisResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class EvidenceAnalyzerClient {
    private final WebClient webClient;

    public EvidenceAnalyzerClient(@Value("${evidence.analyzer.url}") String evidenceAnalyzerUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(evidenceAnalyzerUrl)
                .build();
    }

    public GapAnalysisResponse analyzeGaps(GapAnalysisRequest request) {
        try {
            System.out.println("Calling Evidence Analyzer with " + request.getRequirements().size() + " requirements");
            GapAnalysisResponse response = webClient.post()
                    .uri("/api/analyze/gaps")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(GapAnalysisResponse.class)
                    .block();

            if (response != null && response.getSuggestions() != null) {
                System.out.println("Received " + response.getSuggestions().size() + " AI suggestions");
            }
            return response;
        } catch (Exception e) {
            // Return fallback response if analyzer is unavailable
            System.err.println("Failed to call Evidence Analyzer: " + e.getMessage());
            e.printStackTrace();
            return GapAnalysisResponse.builder()
                    .suggestions(java.util.Arrays.asList(
                        "Upload evidence documents for pending requirements",
                        "Review and complete partially covered requirements"
                    ))
                    .build();
        }
    }
}
