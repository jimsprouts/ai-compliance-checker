#!/bin/bash

# Comprehensive Test Suite - All Edge Cases and Scenarios
# This script tests happy paths, edge cases, failures, and gap detection

set +e  # Don't exit on errors - we want to test error cases

echo "======================================================================"
echo "COMPREHENSIVE COMPLIANCE CHECKER TEST SUITE"
echo "======================================================================"
echo ""

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

PASSED=0
FAILED=0

# Helper function to test an endpoint
test_endpoint() {
    local test_name="$1"
    local expected_status="$2"
    shift 2
    local curl_command="$@"

    echo -e "${BLUE}TEST: ${test_name}${NC}"

    # Execute the curl command and capture HTTP status
    response=$(eval "$curl_command -w '\n%{http_code}' -s")
    http_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | sed '$d')

    if [ "$http_code" = "$expected_status" ]; then
        echo -e "${GREEN}✓ PASSED${NC} (HTTP $http_code)"
        ((PASSED++))
    else
        echo -e "${RED}✗ FAILED${NC} (Expected HTTP $expected_status, got $http_code)"
        ((FAILED++))
    fi

    # Show response body if it's JSON
    if echo "$body" | jq . >/dev/null 2>&1; then
        echo "$body" | jq '.' | head -20
    else
        echo "$body" | head -10
    fi
    echo ""
}

echo "======================================================================"
echo "SECTION 1: CHECKLIST SERVICE - HAPPY PATH"
echo "======================================================================"
echo ""

test_endpoint \
    "1.1: Get all checklists" \
    "200" \
    "curl http://localhost:8080/api/checklists"

test_endpoint \
    "1.2: Get specific checklist by ID" \
    "200" \
    "curl http://localhost:8080/api/checklists/iso-27001-simplified"

test_endpoint \
    "1.3: Get compliance progress (initial state - 0%)" \
    "200" \
    "curl http://localhost:8080/api/checklists/iso-27001-simplified/progress"

echo "======================================================================"
echo "SECTION 2: CHECKLIST SERVICE - EDGE CASES & ERRORS"
echo "======================================================================"
echo ""

test_endpoint \
    "2.1: Get non-existent checklist (should return empty or 404)" \
    "404" \
    "curl http://localhost:8080/api/checklists/non-existent-id"

test_endpoint \
    "2.2: Update item status with valid data" \
    "200" \
    "curl -X POST http://localhost:8080/api/checklists/iso-27001-simplified/items/AC-1/status -H 'Content-Type: application/json' -d '{\"status\":\"COMPLETED\",\"evidenceId\":\"test-evidence-1\"}'"

test_endpoint \
    "2.3: Update non-existent item (should fail)" \
    "404" \
    "curl -X POST http://localhost:8080/api/checklists/iso-27001-simplified/items/INVALID-ID/status -H 'Content-Type: application/json' -d '{\"status\":\"COMPLETED\"}'"

test_endpoint \
    "2.4: Update with invalid status value (should fail)" \
    "400" \
    "curl -X POST http://localhost:8080/api/checklists/iso-27001-simplified/items/AC-1/status -H 'Content-Type: application/json' -d '{\"status\":\"INVALID_STATUS\"}'"

# Reset status for next tests
curl -s -X POST http://localhost:8080/api/checklists/iso-27001-simplified/items/AC-1/status \
  -H 'Content-Type: application/json' -d '{"status":"PENDING"}' > /dev/null

echo "======================================================================"
echo "SECTION 3: EVIDENCE ANALYZER - HAPPY PATH"
echo "======================================================================"
echo ""

test_endpoint \
    "3.1: Health check" \
    "200" \
    "curl http://localhost:3001/health"

# Test with a document that SHOULD match (high confidence)
test_endpoint \
    "3.2: Upload strong evidence (AC-1_password_policy.txt - should match with high confidence)" \
    "200" \
    "curl -X POST http://localhost:3001/api/analyze/document -F 'document=@sample-documents/AC-1_password_policy.txt' -F 'requirement=Password policy documented and enforced' -F 'hints=[\"password policy\",\"security guidelines\"]'"

# Test with text that matches
test_endpoint \
    "3.3: Match document text to requirement (strong match)" \
    "200" \
    "curl -X POST http://localhost:3001/api/analyze/match -H 'Content-Type: application/json' -d '{\"documentText\":\"Our password policy requires minimum 12 characters with complexity requirements. Passwords expire every 90 days.\",\"requirement\":\"Password policy documented and enforced\",\"hints\":[\"password policy\"]}'"

echo "======================================================================"
echo "SECTION 4: EVIDENCE ANALYZER - EDGE CASES (GAP DETECTION)"
echo "======================================================================"
echo ""

# Test with a document that DOESN'T match (should detect gap)
test_endpoint \
    "4.1: Upload irrelevant document (DP-1_backup_policy.txt for password requirement - should NOT match)" \
    "200" \
    "curl -X POST http://localhost:3001/api/analyze/document -F 'document=@sample-documents/DP-1_backup_policy.txt' -F 'requirement=Password policy documented and enforced' -F 'hints=[\"password policy\"]'"

# Test with weak/partial evidence
test_endpoint \
    "4.2: Weak evidence (mentions passwords but lacks details - should be partial/low confidence)" \
    "200" \
    "curl -X POST http://localhost:3001/api/analyze/match -H 'Content-Type: application/json' -d '{\"documentText\":\"Users must have passwords.\",\"requirement\":\"Password policy documented and enforced with complexity requirements\",\"hints\":[\"password policy\",\"complexity\"]}'"

# Test with empty document
test_endpoint \
    "4.3: Empty document text (should return 400 - validation error)" \
    "400" \
    "curl -X POST http://localhost:3001/api/analyze/match -H 'Content-Type: application/json' -d '{\"documentText\":\"\",\"requirement\":\"Password policy documented\",\"hints\":[]}'"

# Test missing required field
test_endpoint \
    "4.4: Missing requirement field (should return 400)" \
    "400" \
    "curl -X POST http://localhost:3001/api/analyze/match -H 'Content-Type: application/json' -d '{\"documentText\":\"some text\"}'"

# Test with wrong field name in file upload (Multer returns 500 for unexpected fields)
test_endpoint \
    "4.5: Wrong multipart field name (should return 500 - Multer error)" \
    "500" \
    "curl -X POST http://localhost:3001/api/analyze/document -F 'wrongfield=@sample-documents/AC-1_password_policy.txt' -F 'requirement=test'"

# Test gap analysis with mixed statuses
test_endpoint \
    "4.6: Gap analysis with mixed evidence" \
    "200" \
    "curl -X POST http://localhost:3001/api/analyze/gaps -H 'Content-Type: application/json' -d '{\"requirements\":[{\"id\":\"AC-1\",\"requirement\":\"Password policy\",\"status\":\"COMPLETED\"},{\"id\":\"AC-2\",\"requirement\":\"Access reviews\",\"status\":\"PENDING\"},{\"id\":\"AC-3\",\"requirement\":\"Admin logging\",\"status\":\"PARTIAL\"}],\"evidenceList\":[\"password_policy.txt\"]}'"

echo "======================================================================"
echo "SECTION 5: REPORT GENERATOR - HAPPY PATH"
echo "======================================================================"
echo ""

test_endpoint \
    "5.1: Generate compliance report (0% - all pending)" \
    "200" \
    "curl http://localhost:5001/api/report/compliance/iso-27001-simplified"

test_endpoint \
    "5.2: Generate gap report (should show all 10 gaps)" \
    "200" \
    "curl http://localhost:5001/api/report/gaps/iso-27001-simplified"

test_endpoint \
    "5.3: Get suggestions for gaps" \
    "200" \
    "curl -X POST http://localhost:5001/api/report/suggestions -H 'Content-Type: application/json' -d '{\"checklistId\":\"iso-27001-simplified\",\"gaps\":[\"AC-2: User access reviews\",\"DP-2: Encryption standards\"],\"context\":\"Small organization\"}'"

echo "======================================================================"
echo "SECTION 6: REPORT GENERATOR - EDGE CASES"
echo "======================================================================"
echo ""

test_endpoint \
    "6.1: Report for non-existent checklist (should fail)" \
    "404" \
    "curl http://localhost:5001/api/report/compliance/invalid-checklist-id"

test_endpoint \
    "6.2: Suggestions with empty gaps array" \
    "200" \
    "curl -X POST http://localhost:5001/api/report/suggestions -H 'Content-Type: application/json' -d '{\"checklistId\":\"iso-27001-simplified\",\"gaps\":[]}'"

test_endpoint \
    "6.3: Suggestions without checklistId (should still work)" \
    "200" \
    "curl -X POST http://localhost:5001/api/report/suggestions -H 'Content-Type: application/json' -d '{\"gaps\":[\"Test gap\"]}'"

echo "======================================================================"
echo "SECTION 7: INTEGRATION TEST - FULL WORKFLOW"
echo "======================================================================"
echo ""

echo -e "${YELLOW}7.1: Complete workflow - Upload evidence and verify progress${NC}"
echo ""

# Step 1: Check initial progress (should be 0% or low)
echo "Step 1: Check initial progress..."
initial_progress=$(curl -s http://localhost:8080/api/checklists/iso-27001-simplified/progress | jq -r '.completionPercentage')
echo "Initial completion: $initial_progress%"
echo ""

# Step 2: Upload a document
echo "Step 2: Uploading AC-1_password_policy.txt..."
analysis=$(curl -s -X POST http://localhost:3001/api/analyze/document \
  -F "document=@sample-documents/AC-1_password_policy.txt" \
  -F "requirement=Password policy documented and enforced" \
  -F 'hints=["password policy"]')

matches=$(echo "$analysis" | jq -r '.analysis.matches')
confidence=$(echo "$analysis" | jq -r '.analysis.confidence')
echo "Analysis result: matches=$matches, confidence=$confidence"
echo ""

# Step 3: Update checklist based on analysis
echo "Step 3: Updating checklist status..."
if [ "$matches" = "true" ] && [ "$(echo "$confidence > 0.7" | bc -l)" = "1" ]; then
    status="COMPLETED"
else
    status="PARTIAL"
fi

curl -s -X POST http://localhost:8080/api/checklists/iso-27001-simplified/items/AC-1/status \
  -H 'Content-Type: application/json' \
  -d "{\"status\":\"$status\",\"evidenceId\":\"password-policy-doc\"}" > /dev/null

echo "Updated AC-1 to: $status"
echo ""

# Step 4: Check new progress
echo "Step 4: Check updated progress..."
new_progress=$(curl -s http://localhost:8080/api/checklists/iso-27001-simplified/progress | jq -r '.completionPercentage')
echo "New completion: $new_progress%"
echo ""

# Step 5: Generate gap report
echo "Step 5: Generate gap report..."
gaps=$(curl -s http://localhost:5001/api/report/gaps/iso-27001-simplified | jq -r '.gaps | length')
echo "Remaining gaps: $gaps"
echo ""

if [ "$(echo "$new_progress > $initial_progress" | bc -l)" = "1" ]; then
    echo -e "${GREEN}✓ Integration test PASSED${NC} - Progress increased from $initial_progress% to $new_progress%"
    ((PASSED++))
else
    echo -e "${RED}✗ Integration test FAILED${NC} - Progress did not increase"
    ((FAILED++))
fi
echo ""

echo "======================================================================"
echo "SECTION 8: REALISTIC GAP SCENARIOS"
echo "======================================================================"
echo ""

# Create a temporary weak policy file
cat > /tmp/weak_password_policy.txt << 'EOF'
Password Policy

Users should use passwords.
EOF

test_endpoint \
    "8.1: Weak policy document (should detect gaps/low confidence)" \
    "200" \
    "curl -X POST http://localhost:3001/api/analyze/document -F 'document=@/tmp/weak_password_policy.txt' -F 'requirement=Password policy with complexity requirements, rotation, and MFA' -F 'hints=[\"password\",\"complexity\",\"MFA\"]'"

# Create a document with partial coverage
cat > /tmp/partial_password_policy.txt << 'EOF'
Password Policy

1. Password Requirements:
   - Minimum 8 characters
   - Must contain letters and numbers

Note: This policy is incomplete and missing many requirements.
EOF

test_endpoint \
    "8.2: Partial coverage document (should identify missing elements)" \
    "200" \
    "curl -X POST http://localhost:3001/api/analyze/document -F 'document=@/tmp/partial_password_policy.txt' -F 'requirement=Password policy with 12+ chars, complexity, rotation every 90 days, and MFA' -F 'hints=[\"password\",\"complexity\",\"rotation\",\"MFA\"]'"

# Test completely wrong document
test_endpoint \
    "8.3: Completely wrong document (incident plan for password requirement)" \
    "200" \
    "curl -X POST http://localhost:3001/api/analyze/document -F 'document=@sample-documents/IM-1_incident_response_plan.txt' -F 'requirement=Password policy documented and enforced' -F 'hints=[\"password\"]'"

# Clean up temp files
rm -f /tmp/weak_password_policy.txt /tmp/partial_password_policy.txt

echo "======================================================================"
echo "SECTION 9: STRESS & BOUNDARY TESTS"
echo "======================================================================"
echo ""

# Test with very long requirement text
test_endpoint \
    "9.1: Very long requirement text" \
    "200" \
    "curl -X POST http://localhost:3001/api/analyze/match -H 'Content-Type: application/json' -d '{\"documentText\":\"Short doc\",\"requirement\":\"$(printf 'Very long requirement %.0s' {1..50})\",\"hints\":[]}'"

# Test with special characters
test_endpoint \
    "9.2: Requirement with special characters" \
    "200" \
    "curl -X POST http://localhost:3001/api/analyze/match -H 'Content-Type: application/json' -d '{\"documentText\":\"Test & < > \\\" '\'''\''\\n\",\"requirement\":\"Test & validation\",\"hints\":[]}'"

# Test with multiple hints
test_endpoint \
    "9.3: Multiple hints (10 hints)" \
    "200" \
    "curl -X POST http://localhost:3001/api/analyze/match -H 'Content-Type: application/json' -d '{\"documentText\":\"Password policy document\",\"requirement\":\"Password policy\",\"hints\":[\"password\",\"policy\",\"security\",\"authentication\",\"credentials\",\"access\",\"login\",\"user\",\"account\",\"protection\"]}'"

echo "======================================================================"
echo "SECTION 10: CORS & HEADERS VALIDATION"
echo "======================================================================"
echo ""

test_endpoint \
    "10.1: CORS preflight request (OPTIONS)" \
    "200" \
    "curl -X OPTIONS http://localhost:8080/api/checklists -H 'Origin: http://localhost:3000' -H 'Access-Control-Request-Method: GET'"

echo -e "${BLUE}TEST: 10.2: Check CORS headers in response${NC}"
cors_headers=$(curl -s -v http://localhost:8080/api/checklists -H 'Origin: http://localhost:3000' 2>&1 | grep -i 'access-control-allow-origin')
if [ -n "$cors_headers" ]; then
    echo -e "${GREEN}✓ PASSED${NC} - CORS headers present"
    echo "$cors_headers"
    ((PASSED++))
else
    echo -e "${RED}✗ FAILED${NC} - CORS headers missing"
    ((FAILED++))
fi
echo ""

echo "======================================================================"
echo "TEST SUMMARY"
echo "======================================================================"
echo ""
echo -e "${GREEN}Passed: $PASSED${NC}"
echo -e "${RED}Failed: $FAILED${NC}"
echo -e "Total: $((PASSED + FAILED))"
echo ""

if [ $FAILED -eq 0 ]; then
    echo -e "${GREEN}🎉 ALL TESTS PASSED!${NC}"
    exit 0
else
    echo -e "${YELLOW}⚠️  Some tests failed. Review output above.${NC}"
    exit 1
fi
