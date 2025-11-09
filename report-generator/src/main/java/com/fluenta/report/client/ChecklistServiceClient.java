package com.fluenta.report.client;

import com.fluenta.report.model.Checklist;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class ChecklistServiceClient {
    private final WebClient webClient;

    public ChecklistServiceClient(@Value("${checklist.service.url}") String checklistServiceUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(checklistServiceUrl)
                .build();
    }

    public Checklist getChecklist(String checklistId) {
        return webClient.get()
                .uri("/api/checklists/{id}", checklistId)
                .retrieve()
                .bodyToMono(Checklist.class)
                .block();
    }
}
