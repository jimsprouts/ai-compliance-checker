# Document Mapping Guide - Which File for Which Requirement?

## Quick Reference Table

| Requirement ID | Requirement Name | Upload This File | Expected Result |
|---------------|------------------|------------------|-----------------|
| **AC-1** | Password policy documented and enforced | `AC-1_password_policy.txt` | ✅ COMPLETED (95%) |
| **AC-2** | User access reviews conducted quarterly | `AC-2_access_review_procedure.txt` | ✅ COMPLETED (90%) |
| **AC-3** | Administrative access logged and monitored | `AC-3_admin_access_logging.txt` | ✅ COMPLETED (90%) |
| **IM-1** | Incident response plan documented | `IM-1_incident_response_plan.txt` | ✅ COMPLETED (95%) |
| **IM-2** | Incident log maintained and reviewed | `IM-2_incident_log_template.txt` | ✅ COMPLETED (90%) |
| **IM-3** | Recovery procedures tested annually | `IM-3_disaster_recovery_testing.txt` | ✅ COMPLETED (90%) |
| **DP-1** | Backup policy defined and implemented | `DP-1_backup_policy.txt` | ✅ COMPLETED (95%) |
| **DP-2** | Encryption standards documented | `DP-2_encryption_standards.txt` | ✅ COMPLETED (95%) |
| **DP-3** | Data retention policy exists and enforced | `DP-3_data_retention_policy.txt` | ✅ COMPLETED (90%) |
| **RM-1** | Risk assessment conducted annually | `RM-1_risk_assessment_report.txt` | ✅ COMPLETED (95%) |

---

## File Naming Convention

### Strong Evidence (Good Documents)
Format: `{REQUIREMENT_ID}_{description}.txt`

Example: `AC-1_password_policy.txt`
- **AC-1** = Requirement ID
- **password_policy** = Document description
- Files are in `sample-documents/` root directory

### Weak Evidence (Incomplete Documents)
Format: `WEAK_{REQUIREMENT_ID}_{description}.txt`

Example: `WEAK_AC-1_password_policy.txt`
- **WEAK** prefix = Indicates incomplete evidence
- Files are in `sample-documents/weak-evidence/` subdirectory

### Wrong Evidence (Irrelevant Documents)
Format: `WRONG_{description}.txt`

Example: `WRONG_vacation_policy.txt`
- **WRONG** prefix = Indicates irrelevant document
- No requirement ID (can be used for any requirement to test rejection)
- Files are in `sample-documents/wrong-evidence/` subdirectory

---

## Directory Structure

```
sample-documents/
├── AC-1_password_policy.txt              ✅ Upload for AC-1
├── AC-2_access_review_procedure.txt      ✅ Upload for AC-2
├── AC-3_admin_access_logging.txt         ✅ Upload for AC-3
├── DP-1_backup_policy.txt                ✅ Upload for DP-1
├── DP-2_encryption_standards.txt         ✅ Upload for DP-2
├── DP-3_data_retention_policy.txt        ✅ Upload for DP-3
├── IM-1_incident_response_plan.txt       ✅ Upload for IM-1
├── IM-2_incident_log_template.txt        ✅ Upload for IM-2
├── IM-3_disaster_recovery_testing.txt    ✅ Upload for IM-3
├── RM-1_risk_assessment_report.txt       ✅ Upload for RM-1
│
├── weak-evidence/
│   ├── WEAK_AC-1_password_policy.txt     ⚠️ Tests gap detection for AC-1
│   ├── WEAK_AC-2_access_review.txt       ⚠️ Tests gap detection for AC-2
│   └── WEAK_DP-2_encryption.txt          ⚠️ Tests gap detection for DP-2
│
└── wrong-evidence/
    ├── WRONG_vacation_policy.txt         ❌ Tests rejection (use for any requirement)
    └── WRONG_employee_handbook.txt       ❌ Tests rejection (use for any requirement)
```

---

## Testing Scenarios

### Scenario 1: 100% Compliance (Happy Path)

**Goal:** Achieve 100% compliance

**Upload sequence:**
1. AC-1 → `AC-1_password_policy.txt` → ✅ COMPLETED
2. AC-2 → `AC-2_access_review_procedure.txt` → ✅ COMPLETED
3. AC-3 → `AC-3_admin_access_logging.txt` → ✅ COMPLETED
4. IM-1 → `IM-1_incident_response_plan.txt` → ✅ COMPLETED
5. IM-2 → `IM-2_incident_log_template.txt` → ✅ COMPLETED
6. IM-3 → `IM-3_disaster_recovery_testing.txt` → ✅ COMPLETED
7. DP-1 → `DP-1_backup_policy.txt` → ✅ COMPLETED
8. DP-2 → `DP-2_encryption_standards.txt` → ✅ COMPLETED
9. DP-3 → `DP-3_data_retention_policy.txt` → ✅ COMPLETED
10. RM-1 → `RM-1_risk_assessment_report.txt` → ✅ COMPLETED

**Result:** Progress bar reaches 100%, Gap Analysis section disappears

---

### Scenario 2: Gap Detection Demo (For Examiner)

**Goal:** Demonstrate gap detection with weak and wrong evidence

**Upload sequence:**
1. AC-1 → `AC-1_password_policy.txt` → ✅ COMPLETED (baseline)
2. AC-2 → `WEAK_AC-2_access_review.txt` → ⚠️ PARTIAL (~40% confidence)
   - **Shows:** Missing elements identified, still in gap list
3. AC-3 → `WRONG_vacation_policy.txt` → ❌ PENDING (rejected, ~5% confidence)
   - **Shows:** AI rejects irrelevant documents
4. DP-2 → `WEAK_DP-2_encryption.txt` → ⚠️ PARTIAL (~35% confidence)
   - **Shows:** Another gap detected

**Result:**
- Progress: ~18% (1 complete, 2 partial)
- Gap Analysis shows 8 gaps (2 PARTIAL, 6 PENDING)
- Demonstrates all 3 states: COMPLETED, PARTIAL, PENDING

---

### Scenario 3: Mixed Compliance (Realistic)

**Goal:** Show realistic partial compliance state

**Upload sequence:**
1. AC-1 → `AC-1_password_policy.txt` → ✅ COMPLETED
2. AC-2 → `AC-2_access_review_procedure.txt` → ✅ COMPLETED
3. AC-3 → `WEAK_AC-1_password_policy.txt` → ⚠️ PARTIAL (wrong doc for AC-3)
4. IM-1 → `IM-1_incident_response_plan.txt` → ✅ COMPLETED
5. DP-1 → `DP-1_backup_policy.txt` → ✅ COMPLETED
6. (Leave IM-2, IM-3, DP-2, DP-3, RM-1 without evidence)

**Result:**
- Progress: ~45% (4 complete, 1 partial, 5 pending)
- Gap Analysis shows realistic compliance state
- Mix of completed, partial, and pending items

---

## Color-Coded Quick Guide

### 🟢 Green = Strong Evidence (Use for 100% compliance)
```
AC-1_password_policy.txt
AC-2_access_review_procedure.txt
AC-3_admin_access_logging.txt
IM-1_incident_response_plan.txt
IM-2_incident_log_template.txt
IM-3_disaster_recovery_testing.txt
DP-1_backup_policy.txt
DP-2_encryption_standards.txt
DP-3_data_retention_policy.txt
RM-1_risk_assessment_report.txt
```

### 🟡 Yellow = Weak Evidence (Use for gap detection demo)
```
weak-evidence/WEAK_AC-1_password_policy.txt  → Test for AC-1 or AC-2
weak-evidence/WEAK_AC-2_access_review.txt    → Test for AC-2
weak-evidence/WEAK_DP-2_encryption.txt       → Test for DP-2
```

### 🔴 Red = Wrong Evidence (Use to test rejection)
```
wrong-evidence/WRONG_vacation_policy.txt     → Upload for ANY requirement
wrong-evidence/WRONG_employee_handbook.txt   → Upload for ANY requirement
```

---

## How to Read File Names

### Example 1: `AC-1_password_policy.txt`
- **AC-1** = For requirement AC-1 (Access Control #1)
- **password_policy** = Document type
- **No prefix** = Strong evidence (will pass)

### Example 2: `WEAK_AC-2_access_review.txt`
- **WEAK** prefix = Incomplete document (will be PARTIAL)
- **AC-2** = Intended for requirement AC-2
- Use this to demonstrate gap detection!

### Example 3: `WRONG_vacation_policy.txt`
- **WRONG** prefix = Irrelevant document (will be rejected)
- **No requirement ID** = Can upload to any requirement
- Use this to demonstrate AI rejection!

---

## Command Line Quick Reference

### List all strong evidence files:
```bash
ls sample-documents/AC-*.txt sample-documents/IM-*.txt sample-documents/DP-*.txt sample-documents/RM-*.txt
```

### List weak evidence files:
```bash
ls sample-documents/weak-evidence/
```

### List wrong evidence files:
```bash
ls sample-documents/wrong-evidence/
```

### Test a specific document via API:
```bash
# Good document
curl -X POST http://localhost:3001/api/analyze/document \
  -F "document=@sample-documents/AC-1_password_policy.txt" \
  -F "requirement=Password policy documented and enforced" \
  -F 'hints=["password policy"]'

# Weak document
curl -X POST http://localhost:3001/api/analyze/document \
  -F "document=@sample-documents/weak-evidence/WEAK_AC-1_password_policy.txt" \
  -F "requirement=Password policy with complexity, rotation, and MFA" \
  -F 'hints=["password policy","complexity","MFA"]'

# Wrong document
curl -X POST http://localhost:3001/api/analyze/document \
  -F "document=@sample-documents/wrong-evidence/WRONG_vacation_policy.txt" \
  -F "requirement=Password policy documented and enforced" \
  -F 'hints=["password policy"]'
```

---

## Checklist for Demo Preparation

Before demonstrating to examiner:

- [ ] Verify all files are renamed with requirement IDs
- [ ] Test uploading `AC-1_password_policy.txt` → Should get ~95% confidence
- [ ] Test uploading `WEAK_AC-1_password_policy.txt` → Should get ~30% confidence
- [ ] Test uploading `WRONG_vacation_policy.txt` → Should get ~5% confidence
- [ ] Confirm file names are visible in file upload dialog
- [ ] Open file explorer to show organized structure

---

## Summary

**Before reorganization:**
```
❌ password_policy.txt → Which requirement is this for? 🤔
❌ backup_policy.txt → Is this DP-1 or DP-3? 🤔
```

**After reorganization:**
```
✅ AC-1_password_policy.txt → Clearly for AC-1! 👍
✅ DP-1_backup_policy.txt → Clearly for DP-1! 👍
✅ WEAK_AC-2_access_review.txt → Weak evidence for AC-2, test gaps! 👍
✅ WRONG_vacation_policy.txt → Wrong doc, test rejection! 👍
```

**Now it's crystal clear which file to upload for which requirement!** 🎯
