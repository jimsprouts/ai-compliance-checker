# API Reference - Compliance Checker

Complete reference for all microservice endpoints.

## Quick Reference

- **Checklist Service**: http://localhost:8080
- **Evidence Analyzer**: http://localhost:3001
- **Report Generator**: http://localhost:5001
- **Frontend**: http://localhost:3000

---

## Checklist Service (Port 8080)

### 1. List All Checklists
```bash
GET /api/checklists
```

**Response:**
```json
[
  {
    "id": "iso-27001-simplified",
    "name": "ISO 27001 Essential Controls",
    "description": "Simplified ISO 27001 compliance checklist",
    "items": [...]
  }
]
```

**Example:**
```bash
curl http://localhost:8080/api/checklists
```

---

### 2. Get Specific Checklist
```bash
GET /api/checklists/{checklistId}
```

**Parameters:**
- `checklistId`: The ID of the checklist (e.g., `iso-27001-simplified`)

**Response:**
```json
{
  "id": "iso-27001-simplified",
  "name": "ISO 27001 Essential Controls",
  "description": "Simplified ISO 27001 compliance checklist",
  "items": [
    {
      "id": "AC-1",
      "category": "Access Control",
      "requirement": "Password policy documented and enforced",
      "hints": ["password policy", "security guidelines", "authentication"],
      "status": "PENDING",
      "evidence": []
    }
  ]
}
```

**Example:**
```bash
curl http://localhost:8080/api/checklists/iso-27001-simplified
```

---

### 3. Get Compliance Progress
```bash
GET /api/checklists/{checklistId}/progress
```

**Parameters:**
- `checklistId`: The ID of the checklist

**Response:**
```json
{
  "checklistId": "iso-27001-simplified",
  "totalItems": 10,
  "completedItems": 2,
  "partialItems": 1,
  "pendingItems": 7,
  "completionPercentage": 25.0
}
```

**Example:**
```bash
curl http://localhost:8080/api/checklists/iso-27001-simplified/progress
```

---

### 4. Update Item Status
```bash
POST /api/checklists/{checklistId}/items/{itemId}/status
```

**Parameters:**
- `checklistId`: The ID of the checklist
- `itemId`: The ID of the item (e.g., `AC-1`)

**Request Body:**
```json
{
  "status": "COMPLETED",
  "evidenceId": "evidence-1"
}
```

**Response:**
```json
{
  "id": "AC-1",
  "category": "Access Control",
  "requirement": "Password policy documented and enforced",
  "status": "COMPLETED",
  "evidence": ["evidence-1"]
}
```

**Example:**
```bash
curl -X POST http://localhost:8080/api/checklists/iso-27001-simplified/items/AC-1/status \
  -H "Content-Type: application/json" \
  -d '{"status":"COMPLETED","evidenceId":"evidence-1"}'
```

---

## Evidence Analyzer (Port 3001)

### 5. Health Check
```bash
GET /health
```

**Response:**
```json
{
  "status": "ok",
  "service": "evidence-analyzer"
}
```

**Example:**
```bash
curl http://localhost:3001/health
```

---

### 6. Upload and Analyze Document
```bash
POST /api/analyze/document
```

**Form Data:**
- `document`: The file to upload (multipart/form-data)
- `requirement`: The compliance requirement text
- `hints`: JSON array of hint strings (optional)

**Response:**
```json
{
  "success": true,
  "documentName": "password_policy.txt",
  "analysis": {
    "matches": true,
    "confidence": 0.95,
    "relevant_sections": ["Section 2.1", "Section 3.4"],
    "reasoning": "The document contains detailed password requirements...",
    "missing_elements": []
  }
}
```

**Example:**
```bash
curl -X POST http://localhost:3001/api/analyze/document \
  -F "document=@sample-documents/password_policy.txt" \
  -F "requirement=Password policy documented and enforced" \
  -F 'hints=["password policy","security guidelines","authentication"]'
```

---

### 7. Match Document Text to Requirement
```bash
POST /api/analyze/match
```

**Request Body:**
```json
{
  "documentText": "Full text of the document...",
  "requirement": "Password policy documented and enforced",
  "hints": ["password policy", "security guidelines"]
}
```

**Response:**
```json
{
  "matches": true,
  "confidence": 0.92,
  "relevant_sections": ["password requirements", "complexity rules"],
  "reasoning": "Document clearly defines password policy...",
  "missing_elements": []
}
```

**Example:**
```bash
curl -X POST http://localhost:3001/api/analyze/match \
  -H "Content-Type: application/json" \
  -d '{
    "documentText": "Our password policy requires minimum 12 characters...",
    "requirement": "Password policy documented and enforced",
    "hints": ["password policy"]
  }'
```

---

### 8. Identify Compliance Gaps
```bash
POST /api/analyze/gaps
```

**Request Body:**
```json
{
  "requirements": [
    {
      "id": "AC-1",
      "requirement": "Password policy documented and enforced",
      "status": "COMPLETED"
    },
    {
      "id": "AC-2",
      "requirement": "User access reviews conducted quarterly",
      "status": "PENDING"
    }
  ],
  "evidenceList": ["password_policy.txt"]
}
```

**Response:**
```json
{
  "gaps": [
    {
      "requirementId": "AC-2",
      "requirement": "User access reviews conducted quarterly",
      "severity": "HIGH",
      "recommendation": "Implement quarterly access review process..."
    }
  ],
  "summary": {
    "totalGaps": 1,
    "criticalGaps": 0,
    "highPriorityGaps": 1
  }
}
```

**Example:**
```bash
curl -X POST http://localhost:3001/api/analyze/gaps \
  -H "Content-Type: application/json" \
  -d '{
    "requirements": [...],
    "evidenceList": ["password_policy.txt"]
  }'
```

---

## Report Generator (Port 5001)

### 9. Generate Compliance Report
```bash
GET /api/report/compliance/{checklistId}
```

**Parameters:**
- `checklistId`: The ID of the checklist

**Response:**
```json
{
  "checklistId": "iso-27001-simplified",
  "checklistName": "ISO 27001 Essential Controls",
  "generatedAt": "2025-11-08T22:59:46.022069543Z",
  "overallStatus": {
    "totalItems": 10,
    "completedItems": 2,
    "partialItems": 1,
    "pendingItems": 7,
    "completionPercentage": 25.0
  },
  "categorySummaries": {
    "Access Control": {
      "category": "Access Control",
      "total": 3,
      "completed": 2,
      "percentage": 66.67
    }
  },
  "completedRequirements": ["AC-1: Password policy documented and enforced"],
  "pendingRequirements": ["AC-2: User access reviews conducted quarterly"],
  "partialRequirements": []
}
```

**Example:**
```bash
curl http://localhost:5001/api/report/compliance/iso-27001-simplified
```

---

### 10. Generate Gap Analysis Report
```bash
GET /api/report/gaps/{checklistId}
```

**Parameters:**
- `checklistId`: The ID of the checklist

**Response:**
```json
{
  "checklistId": "iso-27001-simplified",
  "generatedAt": "2025-11-08T22:59:52.189587671Z",
  "gaps": [
    {
      "requirementId": "AC-2",
      "requirement": "User access reviews conducted quarterly",
      "category": "Access Control",
      "status": "PENDING",
      "reason": "No evidence provided"
    }
  ],
  "criticalGaps": [
    "AC-1: Password policy documented and enforced",
    "DP-2: Encryption standards documented"
  ],
  "recommendations": [
    "Upload evidence documents for pending requirements",
    "Review and complete partially covered requirements",
    "Focus on critical gaps in Access Control and Data Protection first"
  ]
}
```

**Example:**
```bash
curl http://localhost:5001/api/report/gaps/iso-27001-simplified
```

---

### 11. Get AI Improvement Suggestions
```bash
POST /api/report/suggestions
```

**Request Body:**
```json
{
  "checklistId": "iso-27001-simplified",
  "gaps": [
    "AC-2: User access reviews conducted quarterly",
    "DP-2: Encryption standards documented"
  ],
  "context": "Small organization with limited security resources"
}
```

**Response:**
```json
{
  "suggestions": [
    {
      "gap": "AC-2: User access reviews conducted quarterly",
      "recommendation": "Prepare documentation addressing: AC-2: User access reviews conducted quarterly",
      "priority": "HIGH",
      "actionItems": [
        "Create or locate the required documentation",
        "Upload the document for AI analysis",
        "Review and address any gaps identified by the AI"
      ]
    },
    {
      "gap": "DP-2: Encryption standards documented",
      "recommendation": "Prepare documentation addressing: DP-2: Encryption standards documented",
      "priority": "MEDIUM",
      "actionItems": [
        "Create or locate the required documentation",
        "Upload the document for AI analysis",
        "Review and address any gaps identified by the AI"
      ]
    }
  ]
}
```

**Example:**
```bash
curl -X POST http://localhost:5001/api/report/suggestions \
  -H "Content-Type: application/json" \
  -d '{
    "checklistId": "iso-27001-simplified",
    "gaps": ["AC-2: User access reviews conducted quarterly"],
    "context": "Small organization"
  }'
```

---

## Common Issues and Solutions

### Issue: "AC-1" is not a valid checklistId
**Problem:** You're using an item ID instead of a checklist ID.
**Solution:** Use `iso-27001-simplified` as the checklistId. Item IDs (AC-1, IM-1, etc.) are used within the checklist, not as the main ID.

**Wrong:**
```bash
curl http://localhost:5001/api/report/compliance/AC-1
```

**Correct:**
```bash
curl http://localhost:5001/api/report/compliance/iso-27001-simplified
```

---

### Issue: MulterError: Unexpected field
**Problem:** Wrong field name in file upload.
**Solution:** Use `document` as the field name (not `file`).

**Wrong:**
```bash
curl -F "file=@password_policy.txt" ...
```

**Correct:**
```bash
curl -F "document=@password_policy.txt" ...
```

---

### Issue: Connection refused on port 5000
**Problem:** macOS uses port 5000 for AirPlay.
**Solution:** Report Generator is on port 5001.

**Wrong:**
```bash
curl http://localhost:5000/api/report/...
```

**Correct:**
```bash
curl http://localhost:5001/api/report/...
```

---

## Testing Script

Run the comprehensive test script to verify all endpoints:

```bash
./test-endpoints.sh
```

This will test all endpoints and show example responses.

---

## Item IDs Reference

| Item ID | Category | Requirement | Sample Document |
|---------|----------|-------------|-----------------|
| AC-1 | Access Control | Password policy documented and enforced | password_policy.txt |
| AC-2 | Access Control | User access reviews conducted quarterly | access_review_procedure.txt |
| AC-3 | Access Control | Administrative access logged and monitored | admin_access_logging.txt |
| IM-1 | Incident Management | Incident response plan documented | incident_response_plan.txt |
| IM-2 | Incident Management | Incident log maintained and reviewed | incident_log_template.txt |
| IM-3 | Incident Management | Recovery procedures tested annually | disaster_recovery_testing.txt |
| DP-1 | Data Protection | Backup policy defined and implemented | backup_policy.txt |
| DP-2 | Data Protection | Encryption standards documented | encryption_standards.txt |
| DP-3 | Data Protection | Data retention policy exists and enforced | data_retention_policy.txt |
| RM-1 | Risk Management | Risk assessment conducted annually | risk_assessment_report.txt |

---

## Status Values

- `PENDING`: No evidence provided
- `PARTIAL`: Some evidence provided, but incomplete
- `COMPLETED`: Requirement fully satisfied with evidence

---

## Checklist ID

Currently available:
- `iso-27001-simplified`: ISO 27001 Essential Controls (10 requirements)
