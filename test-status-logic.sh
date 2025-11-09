#!/bin/bash

# Test the new "best evidence" status logic
# This script uploads WRONG → STRONG → WEAK documents and verifies status stays COMPLETED

set -e

echo "=========================================================================="
echo "Testing BEST EVIDENCE Status Logic"
echo "=========================================================================="
echo ""

GREEN='\033[0;32m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m'

echo -e "${BLUE}Scenario: Upload WRONG → STRONG → WEAK documents${NC}"
echo "Expected: Status should become COMPLETED after strong doc and STAY COMPLETED"
echo ""

# Step 1: Upload WRONG document (confidence ~0%)
echo "Step 1: Upload WRONG document (employee handbook for password requirement)..."
result=$(curl -s -X POST http://localhost:3001/api/analyze/document \
  -F "document=@sample-documents/wrong-evidence/WRONG_employee_handbook.txt" \
  -F "requirement=Password policy documented and enforced" \
  -F 'hints=["password"]')

confidence=$(echo "$result" | jq -r '.analysis.confidence')
matches=$(echo "$result" | jq -r '.analysis.matches')
echo "  Confidence: $(echo "$confidence * 100" | bc)%"
echo "  Matches: $matches"

# Update checklist
status="PENDING"
curl -s -X POST http://localhost:8080/api/checklists/iso-27001-simplified/items/AC-1/status \
  -H 'Content-Type: application/json' \
  -d "{\"status\":\"$status\",\"evidence\":{\"documentId\":\"test-1\",\"documentName\":\"WRONG_employee_handbook.txt\",\"confidence\":$confidence,\"uploadedAt\":\"$(date -u +"%Y-%m-%dT%H:%M:%S.%3NZ")\",\"relevantSections\":\"\"}}" > /dev/null

current_status=$(curl -s http://localhost:8080/api/checklists/iso-27001-simplified | jq -r '.items[] | select(.id == "AC-1") | .status')
echo "  Status after upload: $current_status"
echo ""

# Step 2: Upload STRONG document (confidence ~95%)
echo "Step 2: Upload STRONG document (AC-1_password_policy.txt)..."
result=$(curl -s -X POST http://localhost:3001/api/analyze/document \
  -F "document=@sample-documents/AC-1_password_policy.txt" \
  -F "requirement=Password policy documented and enforced" \
  -F 'hints=["password"]')

confidence=$(echo "$result" | jq -r '.analysis.confidence')
matches=$(echo "$result" | jq -r '.analysis.matches')
echo "  Confidence: $(echo "$confidence * 100" | bc)%"
echo "  Matches: $matches"

# Update checklist
if [ "$matches" = "true" ] && [ "$(echo "$confidence > 0.7" | bc)" = "1" ]; then
    status="COMPLETED"
elif [ "$(echo "$confidence > 0.3" | bc)" = "1" ]; then
    status="PARTIAL"
else
    status="PENDING"
fi

curl -s -X POST http://localhost:8080/api/checklists/iso-27001-simplified/items/AC-1/status \
  -H 'Content-Type: application/json' \
  -d "{\"status\":\"$status\",\"evidence\":{\"documentId\":\"test-2\",\"documentName\":\"AC-1_password_policy.txt\",\"confidence\":$confidence,\"uploadedAt\":\"$(date -u +"%Y-%m-%dT%H:%M:%S.%3NZ")\",\"relevantSections\":\"password requirements\"}}" > /dev/null

current_status=$(curl -s http://localhost:8080/api/checklists/iso-27001-simplified | jq -r '.items[] | select(.id == "AC-1") | .status')
echo "  Status after upload: $current_status"
echo ""

# Step 3: Upload WEAK document (confidence ~10%)
echo "Step 3: Upload WEAK document (WEAK_AC-1_password_policy.txt)..."
result=$(curl -s -X POST http://localhost:3001/api/analyze/document \
  -F "document=@sample-documents/weak-evidence/WEAK_AC-1_password_policy.txt" \
  -F "requirement=Password policy documented and enforced" \
  -F 'hints=["password"]')

confidence=$(echo "$result" | jq -r '.analysis.confidence')
matches=$(echo "$result" | jq -r '.analysis.matches')
echo "  Confidence: $(echo "$confidence * 100" | bc)%"
echo "  Matches: $matches"

# Update checklist
if [ "$matches" = "true" ] && [ "$(echo "$confidence > 0.7" | bc)" = "1" ]; then
    status="COMPLETED"
elif [ "$(echo "$confidence > 0.3" | bc)" = "1" ]; then
    status="PARTIAL"
else
    status="PENDING"
fi

curl -s -X POST http://localhost:8080/api/checklists/iso-27001-simplified/items/AC-1/status \
  -H 'Content-Type: application/json' \
  -d "{\"status\":\"$status\",\"evidence\":{\"documentId\":\"test-3\",\"documentName\":\"WEAK_AC-1_password_policy.txt\",\"confidence\":$confidence,\"uploadedAt\":\"$(date -u +"%Y-%m-%dT%H:%M:%S.%3NZ")\",\"relevantSections\":\"\"}}" > /dev/null

current_status=$(curl -s http://localhost:8080/api/checklists/iso-27001-simplified | jq -r '.items[] | select(.id == "AC-1") | .status')
echo "  Status after upload: $current_status"
echo ""

# Step 4: Check final state
echo "=========================================================================="
echo "FINAL RESULT"
echo "=========================================================================="
echo ""

final_status=$(curl -s http://localhost:8080/api/checklists/iso-27001-simplified | jq -r '.items[] | select(.id == "AC-1") | .status')
evidence_count=$(curl -s http://localhost:8080/api/checklists/iso-27001-simplified | jq '.items[] | select(.id == "AC-1") | .evidence | length')

echo "Final Status: $final_status"
echo "Total Evidence Count: $evidence_count"
echo ""

curl -s http://localhost:8080/api/checklists/iso-27001-simplified | jq '.items[] | select(.id == "AC-1") | .evidence[] | {name: .documentName, confidence}'

echo ""
if [ "$final_status" = "COMPLETED" ]; then
    echo -e "${GREEN}✓ TEST PASSED${NC} - Status is COMPLETED (based on best evidence)"
    echo "Even though we uploaded a weak document last, the status remains COMPLETED"
    echo "because we have strong evidence (confidence > 0.7) in the evidence list."
else
    echo -e "${RED}✗ TEST FAILED${NC} - Status is $final_status (expected COMPLETED)"
fi
echo ""
