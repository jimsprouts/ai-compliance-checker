package com.fluenta.report.controller;

import com.fluenta.report.model.ComplianceReport;
import com.fluenta.report.model.GapReport;
import com.fluenta.report.model.SuggestionRequest;
import com.fluenta.report.model.SuggestionResponse;
import com.fluenta.report.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/report")
@CrossOrigin(origins = "*")
public class ReportController {
    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/compliance/{checklistId}")
    public ResponseEntity<ComplianceReport> getComplianceReport(@PathVariable String checklistId) {
        try {
            ComplianceReport report = reportService.generateComplianceReport(checklistId);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/gaps/{checklistId}")
    public ResponseEntity<GapReport> getGapReport(@PathVariable String checklistId) {
        try {
            GapReport report = reportService.generateGapReport(checklistId);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/suggestions")
    public ResponseEntity<SuggestionResponse> getSuggestions(@RequestBody SuggestionRequest request) {
        SuggestionResponse response = reportService.generateSuggestions(request);
        return ResponseEntity.ok(response);
    }
}
