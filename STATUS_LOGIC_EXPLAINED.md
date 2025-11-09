# Status Logic - How Evidence Determines Compliance Status

## Overview

The system now uses **BEST EVIDENCE** logic, not **LATEST EVIDENCE** logic. This means:

✅ **Once you upload strong evidence, the status won't degrade** even if you upload weak evidence later.

---

## Status Determination Rules

The system examines **ALL uploaded evidence** for a requirement and uses the **highest confidence** score to determine status:

### Status Formula:

```
maxConfidence = MAX(confidence of all evidence)

IF maxConfidence > 0.7:
    status = COMPLETED ✅
ELSE IF maxConfidence > 0.3:
    status = PARTIAL ⚠️
ELSE:
    status = PENDING ❌
```

---

## Confidence Thresholds

| Document Type | Confidence Range | Status | Behavior |
|--------------|------------------|--------|----------|
| **Strong Evidence** | > 70% | `COMPLETED` | Document fully addresses the requirement |
| **Weak Evidence** | 30-70% | `PARTIAL` | Document mentions requirement but lacks details |
| **Insufficient Evidence** | < 30% | `PENDING` | Document doesn't address requirement |

---

## Examples

### Example 1: Upload Order Doesn't Matter

**Scenario:** Upload WEAK → then STRONG

```
1. Upload WEAK_AC-1_password_policy.txt
   - Confidence: 10%
   - Status becomes: PENDING ❌

2. Upload AC-1_password_policy.txt (strong)
   - Confidence: 95%
   - Status becomes: COMPLETED ✅ (because best confidence is 95%)
```

**Result:** Status is COMPLETED ✅

---

### Example 2: Strong Evidence Protects Status

**Scenario:** Upload STRONG → then WEAK

```
1. Upload AC-1_password_policy.txt (strong)
   - Confidence: 95%
   - Status becomes: COMPLETED ✅

2. Upload WEAK_AC-1_password_policy.txt
   - Confidence: 10%
   - Status STAYS: COMPLETED ✅ (because best confidence is still 95%)
```

**Result:** Status remains COMPLETED ✅

**Why?** The system looks at ALL evidence and uses the BEST one. Even though you uploaded weak evidence later, there's still strong evidence (95%) in the list.

---

### Example 3: Wrong Document Uploaded

**Scenario:** Upload WRONG document

```
1. Upload WRONG_vacation_policy.txt for password requirement
   - Confidence: 5%
   - Matches: false
   - Status becomes: PENDING ❌
```

**Result:** Status is PENDING ❌

---

### Example 4: Multiple Documents of Mixed Quality

**Scenario:** Upload WRONG → STRONG → WEAK → WRONG

```
Evidence list after all uploads:
- WRONG_employee_handbook.txt: 3% confidence
- AC-1_password_policy.txt: 95% confidence  ← BEST
- WEAK_AC-1_password_policy.txt: 15% confidence
- WRONG_vacation_policy.txt: 2% confidence

MAX confidence = 95%
Status = COMPLETED ✅
```

**Result:** Status is COMPLETED ✅ because the best evidence has 95% confidence.

---

## Why This Approach?

### ❌ Old Behavior (Latest Evidence Wins):
```
1. Upload strong doc → COMPLETED ✅
2. Upload weak doc → PARTIAL ⚠️  (degraded!)
```

**Problem:** Your compliance status could degrade just by uploading additional evidence.

### ✅ New Behavior (Best Evidence Wins):
```
1. Upload strong doc → COMPLETED ✅
2. Upload weak doc → COMPLETED ✅ (protected!)
```

**Benefit:** Once you prove compliance with strong evidence, the status stays COMPLETED even if you upload supplementary (but weaker) documents.

---

## Document Types in `sample-documents/`

### Strong Evidence (90-100% confidence)
```
AC-1_password_policy.txt
AC-2_access_review_procedure.txt
AC-3_admin_access_logging.txt
... (all 10 strong docs)
```

**When to use:** Upload these to achieve COMPLETED status

---

### Weak Evidence (10-30% confidence)
```
weak-evidence/WEAK_AC-1_password_policy.txt
weak-evidence/WEAK_AC-2_access_review.txt
weak-evidence/WEAK_DP-2_encryption.txt
```

**When to use:** Test gap detection and PARTIAL/PENDING status

**Content:** Minimal, vague statements like "Employees should use passwords"

---

### Wrong Evidence (0-10% confidence)
```
wrong-evidence/WRONG_employee_handbook.txt
wrong-evidence/WRONG_vacation_policy.txt
```

**When to use:** Test mismatch detection

**Content:** Completely irrelevant topics (vacation policy for password requirement)

---

## Testing the New Logic

### Test 1: Verify Strong Evidence Sets COMPLETED
```bash
1. Go to http://localhost:3000
2. Upload AC-1_password_policy.txt for AC-1
3. Verify status: COMPLETED ✅
```

### Test 2: Verify Status Doesn't Degrade
```bash
1. Upload AC-1_password_policy.txt (strong) → COMPLETED ✅
2. Upload WEAK_AC-1_password_policy.txt (weak)
3. Verify status: STILL COMPLETED ✅
4. Check evidence list: Shows BOTH documents
```

### Test 3: Verify Weak Evidence Alone → PENDING
```bash
1. Start with fresh requirement (no evidence)
2. Upload ONLY WEAK_AC-1_password_policy.txt
3. Verify status: PENDING ❌ (because confidence < 30%)
```

### Test 4: Verify Wrong Evidence → PENDING
```bash
1. Upload WRONG_vacation_policy.txt for AC-1
2. Verify status: PENDING ❌
3. Check analysis: matches = false, confidence < 10%
```

---

## Implementation Details

### Backend Logic (ChecklistService.java:65-82)

```java
private ChecklistItem.ItemStatus determineBestStatus(List<Evidence> evidenceList) {
    if (evidenceList.isEmpty()) {
        return ChecklistItem.ItemStatus.PENDING;
    }

    // Find the HIGHEST confidence among all evidence
    double maxConfidence = evidenceList.stream()
            .mapToDouble(Evidence::getConfidence)
            .max()
            .orElse(0.0);

    // Determine status based on BEST evidence
    if (maxConfidence > 0.7) {
        return ChecklistItem.ItemStatus.COMPLETED;
    } else if (maxConfidence > 0.3) {
        return ChecklistItem.ItemStatus.PARTIAL;
    } else {
        return ChecklistItem.ItemStatus.PENDING;
    }
}
```

**Key points:**
1. **Finds maximum confidence** across all evidence
2. **Applies threshold logic** to max confidence
3. **Returns status** based on best (not latest) evidence

---

## Confidence Score Examples

### Strong Evidence (AC-1_password_policy.txt)
```
Confidence: 95%
Reasoning: "Document contains comprehensive password policy with:
- Length requirements (12+ characters)
- Complexity requirements
- Rotation policy (90 days)
- Account lockout policy
- Password history requirements"
```

### Weak Evidence (WEAK_AC-1_password_policy.txt)
```
Confidence: 10%
Reasoning: "Document mentions passwords but lacks:
- Specific complexity requirements
- Length requirements
- Rotation policy
- Enforcement mechanisms
- Account lockout policy"
```

### Wrong Evidence (WRONG_vacation_policy.txt for AC-1)
```
Confidence: 2%
Matches: false
Reasoning: "Document is about employee vacation time, not password policy"
```

---

## FAQ

### Q: Why do I see multiple documents in the evidence list?
**A:** The system tracks ALL uploaded evidence. This gives auditors a complete history.

### Q: If I upload weak evidence after strong evidence, what happens?
**A:** Status stays COMPLETED because the system uses the BEST (highest confidence) evidence.

### Q: Can I delete evidence?
**A:** Not in this POC. In production, you'd have a "Remove Evidence" button.

### Q: What if I upload 10 weak documents but no strong ones?
**A:** Status will be PENDING or PARTIAL (depending on the highest weak confidence). Only strong evidence (>70%) achieves COMPLETED.

### Q: Why is WEAK_AC-1 only 10% confidence?
**A:** It's intentionally minimal ("Employees should use passwords. Contact IT for help.") to simulate incomplete documentation.

---

## Summary

| Old Logic | New Logic |
|-----------|-----------|
| ❌ Latest upload determines status | ✅ Best upload determines status |
| ❌ Status can degrade | ✅ Status protected by best evidence |
| ❌ Upload order matters | ✅ Upload order doesn't matter |

**Bottom line:** Once you upload strong evidence (>70% confidence), your compliance status is COMPLETED and won't degrade, even if you upload weaker supplementary documents later.
