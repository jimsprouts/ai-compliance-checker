#!/bin/bash

# Compliance Checker - API Endpoint Testing Script
# This script tests all available endpoints in the microservices

set -e

echo "======================================================================"
echo "Compliance Checker - API Endpoint Testing"
echo "======================================================================"
echo ""

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Test Checklist Service (Port 8080)
echo -e "${BLUE}=== CHECKLIST SERVICE (Port 8080) ===${NC}"
echo ""

echo -e "${GREEN}1. GET /api/checklists - List all checklists${NC}"
curl -s http://localhost:8080/api/checklists | jq '.[0] | {id, name, itemCount: (.items | length)}'
echo ""

echo -e "${GREEN}2. GET /api/checklists/iso-27001-simplified - Get specific checklist${NC}"
curl -s http://localhost:8080/api/checklists/iso-27001-simplified | jq '{id, name, description, itemCount: (.items | length)}'
echo ""

echo -e "${GREEN}3. GET /api/checklists/iso-27001-simplified/progress - Get compliance progress${NC}"
curl -s http://localhost:8080/api/checklists/iso-27001-simplified/progress | jq '.'
echo ""

echo -e "${GREEN}4. POST /api/checklists/iso-27001-simplified/items/AC-1/status - Update item status${NC}"
echo "Note: This endpoint updates the status of a checklist item"
echo "Example: curl -X POST -H 'Content-Type: application/json' -d '{\"status\":\"COMPLETED\",\"evidenceId\":\"evidence-1\"}' http://localhost:8080/api/checklists/iso-27001-simplified/items/AC-1/status"
echo ""

# Test Evidence Analyzer (Port 3001)
echo -e "${BLUE}=== EVIDENCE ANALYZER (Port 3001) ===${NC}"
echo ""

echo -e "${GREEN}5. GET /health - Health check${NC}"
curl -s http://localhost:3001/health | jq '.'
echo ""

echo -e "${GREEN}6. POST /api/analyze/document - Upload and analyze document${NC}"
echo "Uploading AC-1_password_policy.txt for AC-1 requirement..."
curl -s -X POST http://localhost:3001/api/analyze/document \
  -F "document=@sample-documents/AC-1_password_policy.txt" \
  -F "requirement=Password policy documented and enforced" \
  -F 'hints=["password policy","security guidelines","authentication"]' | jq '.'
echo ""

echo -e "${GREEN}7. POST /api/analyze/match - Match document text to requirement${NC}"
curl -s -X POST http://localhost:3001/api/analyze/match \
  -H "Content-Type: application/json" \
  -d '{
    "documentText": "Our password policy requires minimum 12 characters with complexity requirements including uppercase, lowercase, numbers and special characters. Passwords must be changed every 90 days.",
    "requirement": "Password policy documented and enforced",
    "hints": ["password policy", "security guidelines"]
  }' | jq '.'
echo ""

echo -e "${GREEN}8. POST /api/analyze/gaps - Identify compliance gaps${NC}"
curl -s -X POST http://localhost:3001/api/analyze/gaps \
  -H "Content-Type: application/json" \
  -d '{
    "requirements": [
      {"id": "AC-1", "requirement": "Password policy documented and enforced", "status": "COMPLETED"},
      {"id": "AC-2", "requirement": "User access reviews conducted quarterly", "status": "PENDING"},
      {"id": "AC-3", "requirement": "Administrative access logged and monitored", "status": "PENDING"}
    ],
    "evidenceList": ["password_policy.txt"]
  }' | jq '.'
echo ""

# Test Report Generator (Port 5001)
echo -e "${BLUE}=== REPORT GENERATOR (Port 5001) ===${NC}"
echo ""

echo -e "${GREEN}9. GET /api/report/compliance/iso-27001-simplified - Full compliance report${NC}"
curl -s http://localhost:5001/api/report/compliance/iso-27001-simplified | jq '{
  checklistId,
  checklistName,
  overallStatus,
  categorySummaries
}'
echo ""

echo -e "${GREEN}10. GET /api/report/gaps/iso-27001-simplified - Gap analysis report${NC}"
curl -s http://localhost:5001/api/report/gaps/iso-27001-simplified | jq '{
  checklistId,
  totalGaps: (.gaps | length),
  criticalGaps: (.criticalGaps | length),
  recommendations
}'
echo ""

echo -e "${GREEN}11. POST /api/report/suggestions - Get AI improvement suggestions${NC}"
curl -s -X POST http://localhost:5001/api/report/suggestions \
  -H "Content-Type: application/json" \
  -d '{
    "checklistId": "iso-27001-simplified",
    "gaps": [
      "AC-2: User access reviews conducted quarterly",
      "DP-2: Encryption standards documented"
    ],
    "context": "Small organization with limited security resources"
  }' | jq '.'
echo ""

echo "======================================================================"
echo "All endpoint tests completed!"
echo "======================================================================"
echo ""
echo "Available Checklist IDs:"
echo "  - iso-27001-simplified"
echo ""
echo "Available Item IDs:"
echo "  - AC-1, AC-2, AC-3 (Access Control)"
echo "  - IM-1, IM-2, IM-3 (Incident Management)"
echo "  - DP-1, DP-2, DP-3 (Data Protection)"
echo "  - RM-1 (Risk Management)"
echo ""
echo "Sample Documents:"
echo "  - sample-documents/password_policy.txt (AC-1)"
echo "  - sample-documents/access_review_procedure.txt (AC-2)"
echo "  - sample-documents/admin_access_logging.txt (AC-3)"
echo "  - sample-documents/incident_response_plan.txt (IM-1)"
echo "  - sample-documents/incident_log_template.txt (IM-2)"
echo "  - sample-documents/disaster_recovery_testing.txt (IM-3)"
echo "  - sample-documents/backup_policy.txt (DP-1)"
echo "  - sample-documents/encryption_standards.txt (DP-2)"
echo "  - sample-documents/data_retention_policy.txt (DP-3)"
echo "  - sample-documents/risk_assessment_report.txt (RM-1)"
echo ""
