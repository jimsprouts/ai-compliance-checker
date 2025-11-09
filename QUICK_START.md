# Quick Start - Upload Guide

## 📁 File Organization (Simple!)

All files are named with **requirement ID prefix** - super easy to find!

```
sample-documents/
├── AC-1_password_policy.txt           → Upload for AC-1
├── AC-2_access_review_procedure.txt   → Upload for AC-2
├── AC-3_admin_access_logging.txt      → Upload for AC-3
├── IM-1_incident_response_plan.txt    → Upload for IM-1
├── IM-2_incident_log_template.txt     → Upload for IM-2
├── IM-3_disaster_recovery_testing.txt → Upload for IM-3
├── DP-1_backup_policy.txt             → Upload for DP-1
├── DP-2_encryption_standards.txt      → Upload for DP-2
├── DP-3_data_retention_policy.txt     → Upload for DP-3
└── RM-1_risk_assessment_report.txt    → Upload for RM-1
```

**Rule:** See "AC-1" on dashboard? Upload file starting with "AC-1_" ✅

---

## 🎯 Requirements to Files Mapping

| Dashboard Shows | Upload This File |
|----------------|------------------|
| **AC-1**: Password policy documented | `AC-1_password_policy.txt` |
| **AC-2**: User access reviews quarterly | `AC-2_access_review_procedure.txt` |
| **AC-3**: Admin access logged | `AC-3_admin_access_logging.txt` |
| **IM-1**: Incident response plan | `IM-1_incident_response_plan.txt` |
| **IM-2**: Incident log maintained | `IM-2_incident_log_template.txt` |
| **IM-3**: Recovery procedures tested | `IM-3_disaster_recovery_testing.txt` |
| **DP-1**: Backup policy defined | `DP-1_backup_policy.txt` |
| **DP-2**: Encryption standards documented | `DP-2_encryption_standards.txt` |
| **DP-3**: Data retention policy | `DP-3_data_retention_policy.txt` |
| **RM-1**: Risk assessment conducted | `RM-1_risk_assessment_report.txt` |

---

## 🚀 5-Step Quick Demo

### 1️⃣ Start Services
```bash
docker-compose up
```

### 2️⃣ Open Dashboard
```
http://localhost:3000
```

### 3️⃣ Upload Your First Document
1. Find **AC-1: Password policy documented and enforced**
2. Click **"Upload Evidence"**
3. Select `sample-documents/AC-1_password_policy.txt`
4. Click **"Analyze & Upload"**

### 4️⃣ Watch AI Analysis
```
✓ Matches: Yes
✓ Confidence: 95%
✓ Reasoning: "Document contains detailed password requirements..."
```

### 5️⃣ See Progress Update
- AC-1 status: PENDING → **COMPLETED** ✅
- Progress: 0% → **10%** 📈
- Gaps: 10 → **9** 📉

**Continue for all 10 requirements to reach 100%!**

---

## 🧪 Testing Gap Detection

### Test 1: Good Document (Should Pass)
```
Requirement: AC-1
Upload: AC-1_password_policy.txt
Expected: ✅ COMPLETED, 95% confidence
```

### Test 2: Weak Document (Should Detect Gap)
```
Requirement: AC-1
Upload: weak-evidence/WEAK_AC-1_password_policy.txt
Expected: ⚠️ PARTIAL, 30% confidence, lists missing elements
```

### Test 3: Wrong Document (Should Reject)
```
Requirement: AC-1
Upload: wrong-evidence/WRONG_vacation_policy.txt
Expected: ❌ PENDING, 5% confidence, matches: false
```

---

## 📋 Complete Upload Checklist

Print this and check off as you upload:

- [ ] **AC-1** → `AC-1_password_policy.txt`
- [ ] **AC-2** → `AC-2_access_review_procedure.txt`
- [ ] **AC-3** → `AC-3_admin_access_logging.txt`
- [ ] **IM-1** → `IM-1_incident_response_plan.txt`
- [ ] **IM-2** → `IM-2_incident_log_template.txt`
- [ ] **IM-3** → `IM-3_disaster_recovery_testing.txt`
- [ ] **DP-1** → `DP-1_backup_policy.txt`
- [ ] **DP-2** → `DP-2_encryption_standards.txt`
- [ ] **DP-3** → `DP-3_data_retention_policy.txt`
- [ ] **RM-1** → `RM-1_risk_assessment_report.txt`

**Progress: 100% = All checkboxes ticked!** 🎉

---

## 💡 Pro Tips

1. **File names match requirement IDs** - No guessing needed!
2. **Upload in any order** - Progress updates automatically
3. **Files in `weak-evidence/`** - Start with `WEAK_` prefix
4. **Files in `wrong-evidence/`** - Start with `WRONG_` prefix
5. **Clear browser cache** if changes don't appear (Cmd+Shift+R)

---

## 🆘 Troubleshooting

### "Which file for AC-2?"
→ Look for file starting with `AC-2_`

### "Can't find requirement ID?"
→ Check dashboard - shows "AC-1", "IM-2", etc.

### "Wrong confidence score?"
→ Make sure you uploaded the matching file:
- AC-1 requirement? → Upload AC-1_ file
- Not DP-1_ file!

### "Progress not updating?"
→ Refresh page or check if analysis returned error

---

## 🎯 Success Criteria

You've succeeded when:
- ✅ All 10 requirements show **COMPLETED** status (green)
- ✅ Progress bar shows **100%**
- ✅ Gap Analysis section **disappears** (no gaps!)
- ✅ All files uploaded correctly

**Time to 100%: ~5 minutes** (with all strong evidence files)

---

## 📖 More Information

- **Full documentation:** See `DOCUMENT_MAPPING_GUIDE.md`
- **Demo script:** See `DEMO_SCRIPT.md`
- **Testing guide:** See `TESTING_WITH_GAPS.md`

---

**That's it! File naming is now super simple - requirement ID matches file prefix!** 🎯
