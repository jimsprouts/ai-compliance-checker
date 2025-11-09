# Testing Guide - Which Script to Use?

## Quick Comparison

| Feature | test-endpoints.sh | test-comprehensive.sh |
|---------|------------------|----------------------|
| **Purpose** | Quick smoke test | Full validation suite |
| **Tests** | 11 basic tests | 40+ tests with edge cases |
| **Duration** | ~10 seconds | ~30-60 seconds |
| **Focus** | "Does it work?" | "Does it handle everything?" |
| **Use Case** | Development, quick checks | Demo, exam, validation |
| **Gap Testing** | ❌ No | ✅ Yes (critical!) |
| **Error Cases** | ❌ No | ✅ Yes |
| **When to Use** | After code changes | Before submitting/demo |

---

## test-endpoints.sh - Quick Smoke Test

### What it does:
Tests the **happy path** only - assumes everything works perfectly.

### Tests included:
1. Get all checklists ✓
2. Get specific checklist ✓
3. Get progress ✓
4. Upload password_policy.txt ✓
5. Match document text ✓
6. Gap analysis ✓
7. Compliance report ✓
8. Gap report ✓
9. Suggestions ✓
10. Health checks ✓

### What it DOESN'T test:
- ❌ Gap detection (wrong documents)
- ❌ Error handling (404, 400 errors)
- ❌ Weak evidence detection
- ❌ Edge cases
- ❌ Integration workflows

### When to use:
```bash
./test-endpoints.sh
```

**Use this when:**
- You just made a code change and want to verify nothing broke
- Quick sanity check before committing
- Testing if services are running
- Development workflow

**Example output:**
```
=== CHECKLIST SERVICE (Port 8080) ===
1. GET /api/checklists - ✓ Works
2. GET /api/checklists/iso-27001-simplified - ✓ Works
...
All endpoint tests completed!
```

---

## test-comprehensive.sh - Full Validation Suite

### What it does:
Tests **everything** including edge cases, errors, and gap detection.

### Test sections (10 sections, 40+ tests):

#### Section 1-3: Happy Path (Same as basic script)
- All endpoints work correctly

#### Section 4: **GAP DETECTION** ⚠️ CRITICAL!
- Wrong document uploaded (should detect mismatch)
- Weak evidence (should identify missing elements)
- Empty documents
- Invalid inputs

#### Section 5-6: Reports & Edge Cases
- Error handling (404s, 400s)
- Missing fields
- Invalid IDs

#### Section 7: **Integration Test**
- Complete end-to-end workflow
- Verifies progress increases after upload

#### Section 8: **Realistic Gap Scenarios** ⚠️ MOST IMPORTANT!
- Weak policy (1 sentence)
- Partial coverage (missing some requirements)
- Completely wrong document

#### Section 9: Stress & Boundary Tests
- Long inputs
- Special characters
- SQL injection attempts

#### Section 10: CORS & Headers
- Frontend-backend communication validation

### When to use:
```bash
./test-comprehensive.sh
```

**Use this when:**
- **Preparing for examiner demo**
- Before submitting the assignment
- Validating the system actually works
- Need to prove gap detection works

**Example output:**
```
======================================================================
COMPREHENSIVE COMPLIANCE CHECKER TEST SUITE
======================================================================

SECTION 4: EVIDENCE ANALYZER - EDGE CASES (GAP DETECTION)
TEST: 4.1: Upload irrelevant document (backup_policy.txt for password requirement - should NOT match)
✓ PASSED (HTTP 200)
{
  "analysis": {
    "matches": false,
    "confidence": 0.15,
    "reasoning": "Document discusses backup procedures, not password policies"
  }
}

...

TEST SUMMARY
Passed: 38
Failed: 2
Total: 40
```

---

## The KEY Difference: Gap Detection

### test-endpoints.sh:
Only uploads **correct** documents for each requirement:
```bash
# password_policy.txt → Password requirement ✓
# This always matches! No gap testing.
```

### test-comprehensive.sh:
Tests what happens with **wrong** and **weak** documents:

```bash
# Test 4.1: Wrong document
backup_policy.txt → Password requirement ✗
Expected: matches=false, low confidence
→ This proves gap detection works!

# Test 8.1: Weak document
"Users should use passwords." → Password policy requirement
Expected: partial match, missing elements listed
→ This proves the AI doesn't just say "yes" to everything!

# Test 8.3: Completely wrong
incident_response_plan.txt → Password requirement
Expected: matches=false, clear reasoning
→ This proves intelligent matching!
```

---

## Real-World Example

### Scenario: Examiner uploads weak evidence

**With test-endpoints.sh:**
- You only tested good documents
- Examiner uploads weak document
- System might incorrectly accept it
- ❌ **FAIL** - Gap detection doesn't work

**With test-comprehensive.sh:**
- You already tested weak/wrong documents
- You know the AI detects gaps correctly
- Examiner uploads weak document
- System correctly identifies missing elements
- ✅ **PASS** - Gap detection proven

---

## Which One Should You Use for the Exam?

### Before the demo: Run BOTH

1. **First:** `./test-endpoints.sh` (quick check everything works)
2. **Then:** `./test-comprehensive.sh` (validate gap detection)

### During the demo: Show test-comprehensive.sh results

The examiner wants to see:
- ✅ Gap detection works (Section 4, 8)
- ✅ Error handling works (Section 2, 6)
- ✅ Integration works (Section 7)

### If you only have time for one: Use test-comprehensive.sh

It includes all the tests from test-endpoints.sh **PLUS** the critical gap detection tests.

---

## How to Demonstrate Gap Detection to Examiner

### Step 1: Show the test script
```bash
cat test-comprehensive.sh | grep -A 5 "4.1:"
```

Shows: "Upload irrelevant document - should NOT match"

### Step 2: Run the test
```bash
./test-comprehensive.sh | grep -A 10 "4.1:"
```

Shows:
```
TEST: 4.1: Upload irrelevant document
✓ PASSED (HTTP 200)
{
  "matches": false,
  "confidence": 0.15,
  "reasoning": "Document discusses backup procedures, not password policies",
  "missing_elements": ["All password policy requirements"]
}
```

### Step 3: Explain
"See? When I upload backup_policy.txt for a password requirement, the AI correctly identifies it doesn't match. This proves the system detects compliance gaps!"

---

## Summary: When to Use Each Script

| Situation | Use This |
|-----------|----------|
| Quick check after code change | test-endpoints.sh |
| Before committing code | test-endpoints.sh |
| Before demo/submission | test-comprehensive.sh |
| Proving gap detection works | test-comprehensive.sh |
| Examiner asks "Does it handle errors?" | test-comprehensive.sh |
| Examiner asks "Can it detect weak evidence?" | test-comprehensive.sh |
| You have 30 seconds | test-endpoints.sh |
| You have 2 minutes | test-comprehensive.sh |

---

## My Recommendation

**Keep both scripts:**

1. **test-endpoints.sh** = Development tool (fast feedback loop)
2. **test-comprehensive.sh** = Validation tool (proves it works)

But for the **exam demo**, definitely use **test-comprehensive.sh** because it proves the system actually solves the compliance problem by detecting gaps!

---

## Quick Commands

```bash
# Quick check (10 seconds)
./test-endpoints.sh

# Full validation (30-60 seconds)
./test-comprehensive.sh

# Test only gap detection (Section 4 & 8)
./test-comprehensive.sh | grep -A 5 "SECTION 4\|SECTION 8"

# Count passed/failed
./test-comprehensive.sh | tail -5
```
