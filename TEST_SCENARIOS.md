# Test Scenarios Documentation

This document explains all test cases in the comprehensive test suite, including what they test and why they're important.

## Test Philosophy

The comprehensive test suite (`test-comprehensive.sh`) covers:
1. **Happy Path** - Normal, expected usage
2. **Edge Cases** - Boundary conditions and unusual inputs
3. **Error Cases** - Invalid inputs and error handling
4. **Gap Detection** - The core feature: identifying missing/weak compliance evidence
5. **Integration** - End-to-end workflows
6. **Stress Tests** - System limits and performance

---

## Section 1: Checklist Service - Happy Path

### 1.1: Get All Checklists
**What it tests:** Basic GET endpoint returning all available checklists
**Expected result:** HTTP 200 with array containing "iso-27001-simplified"
**Why important:** Verifies the service is running and has data loaded

### 1.2: Get Specific Checklist by ID
**What it tests:** Retrieving a single checklist with all 10 requirements
**Expected result:** HTTP 200 with full checklist details (10 items)
**Why important:** Ensures detailed checklist data is accessible

### 1.3: Get Compliance Progress
**What it tests:** Progress calculation when no evidence uploaded
**Expected result:** HTTP 200 with 0% completion, 10 pending items
**Why important:** Verifies initial state and progress calculation logic

---

## Section 2: Checklist Service - Edge Cases & Errors

### 2.1: Non-Existent Checklist
**What it tests:** How system handles invalid checklist ID
**Expected result:** HTTP 404 or empty response
**Why important:** Proper error handling prevents crashes
**Examiner will test:** Invalid IDs, SQL injection attempts

### 2.2: Update Item Status (Valid)
**What it tests:** Marking a requirement as COMPLETED
**Expected result:** HTTP 200 with updated status
**Why important:** Core feature - tracking compliance progress

### 2.3: Update Non-Existent Item
**What it tests:** Updating an item that doesn't exist (e.g., "INVALID-ID")
**Expected result:** HTTP 404
**Why important:** Validates item existence before updating
**Examiner will test:** XYZ-999, special characters in IDs

### 2.4: Invalid Status Value
**What it tests:** Sending invalid status (e.g., "INVALID_STATUS" instead of COMPLETED/PARTIAL/PENDING)
**Expected result:** HTTP 400 Bad Request
**Why important:** Input validation - only accept defined enum values
**Examiner will test:** Random strings, numbers, null values

---

## Section 3: Evidence Analyzer - Happy Path

### 3.1: Health Check
**What it tests:** Service availability
**Expected result:** HTTP 200 with `{"status":"ok"}`
**Why important:** Quick way to verify service is running

### 3.2: Strong Evidence Upload
**What it tests:** Uploading a document that CLEARLY satisfies the requirement
**Example:** password_policy.txt uploaded for "Password policy" requirement
**Expected result:** HTTP 200 with `matches: true`, confidence > 0.8
**Why important:** Verifies AI correctly identifies GOOD compliance evidence

**AI Analysis Example:**
```json
{
  "matches": true,
  "confidence": 0.95,
  "reasoning": "Document contains detailed password requirements, rotation policy, and MFA",
  "missing_elements": []
}
```

### 3.3: Text Matching (Strong)
**What it tests:** Matching clear, relevant text to a requirement
**Example:** "Password policy requires 12 chars, expires every 90 days" → "Password policy"
**Expected result:** High confidence match
**Why important:** Core AI matching functionality

---

## Section 4: Evidence Analyzer - GAP DETECTION (Most Important!)

### 4.1: Wrong Document Upload ⚠️ **CRITICAL GAP TEST**
**What it tests:** Uploading IRRELEVANT document
**Example:** backup_policy.txt uploaded for "Password policy" requirement
**Expected result:** `matches: false` or very low confidence
**Why important:** This is GAP DETECTION! System must identify when evidence doesn't match.

**What examiner will look for:**
```json
{
  "matches": false,
  "confidence": 0.15,
  "reasoning": "Document discusses backup procedures, not password policies",
  "missing_elements": ["password complexity", "rotation policy", "authentication"]
}
```

This proves the AI isn't just saying "yes" to everything!

### 4.2: Weak Evidence (Partial Gap) ⚠️ **CRITICAL**
**What it tests:** Document mentions requirement but lacks details
**Example:** "Users must have passwords" (too vague)
**Expected result:** `matches: true` but LOW confidence (0.3-0.5), status becomes PARTIAL
**Why important:** Detects INCOMPLETE compliance (partial gap)

**AI should identify:**
```json
{
  "matches": true,
  "confidence": 0.35,
  "reasoning": "Document mentions passwords but lacks specific requirements",
  "missing_elements": [
    "Password complexity rules (uppercase, lowercase, numbers, symbols)",
    "Minimum length requirement",
    "Password expiration/rotation policy",
    "Multi-factor authentication"
  ]
}
```

### 4.3: Empty Document
**What it tests:** Handling empty/blank documents gracefully
**Expected result:** HTTP 400 with error message "documentText and requirement are required"
**Why important:** Input validation - rejects invalid data before processing
**Good behavior:** Returns 400 (bad request) instead of processing empty content

### 4.4: Missing Required Field
**What it tests:** API validation - missing "requirement" field
**Expected result:** HTTP 400 with error message
**Why important:** API contract enforcement

### 4.5: Wrong Multipart Field Name
**What it tests:** Upload with "file" instead of "document" field name
**Expected result:** HTTP 400/500 with clear error
**Why important:** Clear error messages help debugging

### 4.6: Gap Analysis with Mixed Statuses
**What it tests:** Analyzing requirements with different statuses (COMPLETED, PENDING, PARTIAL)
**Expected result:** Identifies which gaps are critical vs minor
**Why important:** Prioritizes what to fix first

**Example Response:**
```json
{
  "gaps": [
    {
      "requirementId": "AC-2",
      "status": "PENDING",
      "severity": "HIGH"
    },
    {
      "requirementId": "AC-3",
      "status": "PARTIAL",
      "severity": "MEDIUM"
    }
  ]
}
```

---

## Section 5: Report Generator - Happy Path

### 5.1: Compliance Report (0% State)
**What it tests:** Report generation when nothing is uploaded
**Expected result:** 0% completion, all items pending
**Why important:** Baseline report

### 5.2: Gap Report (All Gaps)
**What it tests:** Listing all 10 gaps when nothing uploaded
**Expected result:** 10 gaps, recommendations to upload evidence
**Why important:** Shows what needs to be done

### 5.3: Improvement Suggestions
**What it tests:** AI-generated action items for fixing gaps
**Expected result:** Specific suggestions per gap
**Why important:** Helps users fix compliance issues

---

## Section 6: Report Generator - Edge Cases

### 6.1: Non-Existent Checklist
**What it tests:** Report for invalid checklist ID
**Expected result:** HTTP 404
**Why important:** Error handling

### 6.2: Empty Gaps Array
**What it tests:** Suggestions when no gaps provided
**Expected result:** Empty suggestions or generic advice
**Why important:** Handles edge case gracefully

### 6.3: Missing Optional Field
**What it tests:** Suggestions without checklistId (optional field)
**Expected result:** Still works, just less specific
**Why important:** Validates optional vs required fields

---

## Section 7: Integration Test - Full Workflow

**What it tests:** Complete end-to-end flow:
1. Check initial progress (0%)
2. Upload document
3. AI analyzes it
4. Update checklist based on AI result
5. Verify progress increased
6. Generate gap report

**Expected flow:**
```
Initial: 0% → Upload password_policy.txt → AI: 95% match →
Update AC-1 to COMPLETED → New: 10% → Gaps reduced from 10 to 9
```

**Why important:** Proves all services work together correctly

---

## Section 8: Realistic Gap Scenarios ⚠️ **EXAMINER'S FOCUS**

### 8.1: Weak Policy Document
**What it tests:** Document with minimal content
**Example:** "Users should use passwords." (one sentence)
**Expected AI behavior:**
- Low confidence (0.2-0.4)
- Identifies MANY missing elements
- Recommends improvements

**What examiner wants to see:**
```json
{
  "matches": false,
  "confidence": 0.25,
  "missing_elements": [
    "Specific password complexity requirements",
    "Minimum password length",
    "Password expiration policy",
    "Account lockout policy",
    "Multi-factor authentication requirements",
    "Password history (reuse prevention)"
  ],
  "reasoning": "Document is too vague and lacks enforceable requirements"
}
```

### 8.2: Partial Coverage Document
**What it tests:** Document that covers SOME requirements but not all
**Example:**
```
Password Policy:
1. Minimum 8 characters
2. Must contain letters and numbers
(Missing: rotation, MFA, lockout policy)
```

**Expected AI behavior:**
- Partial match (confidence 0.5-0.7)
- Lists specifically what's missing
- Status becomes PARTIAL (not COMPLETED)

**Critical for examiner:** Shows the system detects INCOMPLETE compliance, not just missing compliance!

### 8.3: Completely Wrong Document
**What it tests:** Document that's totally irrelevant
**Example:** Uploading "incident_response_plan.txt" for "Password policy" requirement
**Expected AI behavior:**
- `matches: false`
- Very low confidence (< 0.2)
- Clear explanation why it doesn't match

**What proves quality:**
```json
{
  "matches": false,
  "confidence": 0.05,
  "reasoning": "Document describes incident response procedures. It does not address password policies, complexity requirements, or authentication controls.",
  "relevant_sections": [],
  "missing_elements": ["All password policy requirements"]
}
```

---

## Section 9: Stress & Boundary Tests

### 9.1: Very Long Requirement Text
**What it tests:** System handling of unusually long input
**Expected result:** Works without timeout or truncation
**Why important:** Prevents DoS, validates input limits

### 9.2: Special Characters
**What it tests:** SQL injection, XSS prevention
**Example:** `& < > " ' \n` in requirements
**Expected result:** Properly escaped, no errors
**Why important:** Security - prevents injection attacks

### 9.3: Many Hints
**What it tests:** Array handling with 10+ items
**Expected result:** All hints processed correctly
**Why important:** Validates array handling logic

---

## Section 10: CORS & Headers

### 10.1-10.2: CORS Validation
**What it tests:** Frontend can call backend from different port
**Expected result:** CORS headers present, preflight succeeds
**Why important:** Required for microservices architecture

---

## What Will the Examiner Test?

### 1. **Gap Detection Accuracy** (Most Critical!)
The examiner will upload:
- ✅ Perfect documents → Should get high confidence, COMPLETED status
- ⚠️ Weak documents → Should get low confidence, PARTIAL status, missing_elements list
- ❌ Wrong documents → Should get matches: false, clear reasoning

**How to demonstrate this:**
```bash
# Good evidence
curl -F "document=@password_policy.txt" -F "requirement=Password policy" ...
→ Confidence: 95%, Status: COMPLETED

# Weak evidence
curl -F "document=@weak_policy.txt" -F "requirement=Password policy with complexity, rotation, MFA" ...
→ Confidence: 35%, Status: PARTIAL, Missing: [MFA, rotation, complexity details]

# Wrong evidence
curl -F "document=@backup_policy.txt" -F "requirement=Password policy" ...
→ Confidence: 10%, Status: PENDING, matches: false
```

### 2. **Error Handling**
- Invalid IDs → 404
- Invalid status values → 400
- Missing fields → 400
- Service down → Graceful degradation

### 3. **Integration**
- Upload document → AI analyzes → Checklist updates → Progress increases → Gap report reflects changes

### 4. **Realistic Scenarios**
- Partial compliance (7/10 requirements met)
- Mixed quality evidence (some strong, some weak)
- Multiple documents for one requirement

---

## How to Run the Tests

### Quick test (original):
```bash
./test-endpoints.sh
```

### Comprehensive test (all scenarios):
```bash
./test-comprehensive.sh
```

### Individual test:
```bash
# Test gap detection with wrong document
curl -X POST http://localhost:3001/api/analyze/document \
  -F "document=@sample-documents/backup_policy.txt" \
  -F "requirement=Password policy documented and enforced" \
  -F 'hints=["password policy"]' | jq '.analysis'
```

---

## Expected Results Summary

| Test Type | Expected Behavior |
|-----------|------------------|
| Strong Evidence | matches: true, confidence > 0.8, status: COMPLETED |
| Weak Evidence | matches: true, confidence 0.3-0.6, status: PARTIAL, missing_elements listed |
| Wrong Evidence | matches: false, confidence < 0.3, status: PENDING |
| No Evidence | status: PENDING, appears in gap report |
| Invalid Input | HTTP 400, clear error message |
| Missing Resource | HTTP 404 |
| Service Error | HTTP 500, error logged |

---

## Key Takeaways for Examiner Demo

1. **Show gap detection works:**
   - Upload wrong doc → AI says "no match"
   - Upload weak doc → AI says "partial match, missing X, Y, Z"
   - Upload good doc → AI says "strong match"

2. **Show progress tracking:**
   - Start: 0%
   - Upload 3 good docs → 30%
   - Upload 7 more → 100%

3. **Show gap reporting:**
   - Gap report lists what's missing
   - Suggestions provide action items
   - Critical gaps highlighted

4. **Show error handling:**
   - Invalid IDs return 404
   - Invalid inputs return 400
   - System doesn't crash

This demonstrates a production-quality POC that actually solves the compliance problem!
