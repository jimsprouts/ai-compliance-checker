# Compliance Checker - User Guide

## Quick Start Tutorial

### Step 1: Open the Application
Navigate to: http://localhost:3000

You'll see the **Compliance Checker Dashboard** with:
- Title: "ISO 27001 Essential Controls"
- Progress bar at 0%
- 10 compliance requirements listed

---

### Step 2: Understanding the Requirements

The checklist has 10 requirements across 4 categories:

**Access Control (AC)**
- AC-1: Password policy documented and enforced
- AC-2: User access reviews conducted quarterly
- AC-3: Administrative access logged and monitored

**Incident Management (IM)**
- IM-1: Incident response plan documented
- IM-2: Incident log maintained and reviewed
- IM-3: Recovery procedures tested annually

**Data Protection (DP)**
- DP-1: Backup policy defined and implemented
- DP-2: Encryption standards documented
- DP-3: Data retention policy exists and enforced

**Risk Management (RM)**
- RM-1: Risk assessment conducted annually

---

### Step 3: Upload Your First Evidence Document

**Example: Proving you have a password policy**

1. **Find AC-1** in the requirements list
   - Look for: "Password policy documented and enforced"
   - Status badge shows: **PENDING** (red)

2. **Click "Upload Evidence"** button
   - A modal dialog opens
   - Shows the requirement text

3. **Select the document**
   - Click "Choose File"
   - Navigate to: `sample-documents/password_policy.txt`
   - Click Open

4. **Start Analysis**
   - You'll see: "Selected: password_policy.txt"
   - Click **"Analyze & Upload"** button
   - Button changes to "Analyzing..." (disabled)

5. **Wait for AI Analysis** (5-10 seconds)
   - AI reads the document
   - Compares it to the requirement
   - Calculates confidence score

6. **Review Results**
   ```
   Analysis Result
   ✅ Matches: Yes
   📊 Confidence: 92.5%
   💭 Reasoning: "Document contains comprehensive password policy
       including complexity requirements (12 char minimum, mixed case,
       numbers, symbols), rotation policies (90 days for users, 60 for
       admins), and account lockout rules (5 attempts, 30 min lockout)."
   ⚠️ Missing Elements: []
   ```

7. **Close Modal**
   - Click "Cancel" or click outside the modal
   - The requirement now shows **COMPLETED** status

---

### Step 4: Check Your Progress

After uploading the password policy, you'll see:

**Progress Bar:**
```
███░░░░░░░ 10.0%
```

**Statistics:**
- Total: 10
- Completed: 1 (green)
- Partial: 0 (yellow)
- Pending: 9 (red)

**AC-1 Requirement:**
- Status: **COMPLETED** (green badge)
- Evidence: 📄 password_policy.txt (93% confidence)

---

### Step 5: Upload More Evidence

**For Incident Management (IM-1):**
1. Find **IM-1: "Incident response plan documented"**
2. Click "Upload Evidence"
3. Select: `sample-documents/incident_response_plan.txt`
4. Click "Analyze & Upload"
5. Review results (should be 85-95% confidence)
6. Progress updates to **20%**

**For Data Protection (DP-1):**
1. Find **DP-1: "Backup policy defined and implemented"**
2. Click "Upload Evidence"
3. Select: `sample-documents/backup_policy.txt`
4. Click "Analyze & Upload"
5. Review results
6. Progress updates to **30%**

---

### Step 6: Understanding AI Analysis Results

The AI provides 4 key insights:

#### 1. **Matches** (Yes/No)
- **Yes**: Document addresses the requirement
- **No**: Document doesn't cover this requirement

#### 2. **Confidence Score** (0-100%)
- **90-100%**: Excellent match, complete coverage
- **70-89%**: Good match, minor gaps
- **50-69%**: Partial match, significant gaps
- **0-49%**: Poor match, doesn't satisfy requirement

#### 3. **Reasoning**
Explains WHY the AI made its decision:
- What parts of the document match
- What specific requirements are addressed
- How well the document covers the topic

#### 4. **Missing Elements**
Lists what's NOT in the document:
- "Multi-factor authentication not mentioned"
- "Password history requirements missing"
- "Service account policies not specified"

---

### Step 7: View Gap Analysis

Scroll to the bottom of the page to see **Gap Analysis**:

```
Gap Analysis
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Total Gaps: 7
Critical Gaps: 3

Recommendations:
• Upload evidence documents for pending requirements
• Review and complete partially covered requirements
• Focus on critical gaps in Access Control and Data Protection first
```

This tells you:
- How many requirements still need evidence
- Which gaps are most critical
- What to do next

---

## Status Badge Colors

**🔴 PENDING** (Red)
- No evidence uploaded yet
- Requirement not addressed

**🟡 PARTIAL** (Yellow)
- Evidence uploaded but incomplete
- Confidence score 50-70%
- Some gaps identified

**🟢 COMPLETED** (Green)
- Evidence uploaded and approved
- Confidence score > 70%
- Requirement satisfied

---

## Tips for Best Results

### ✅ DO:
- Upload documents that directly address the requirement
- Use the provided sample documents to test
- Review AI reasoning to understand the analysis
- Upload additional documents if confidence is low

### ❌ DON'T:
- Upload unrelated documents
- Expect 100% accuracy from AI (it's a tool, not perfect)
- Ignore the "Missing Elements" section
- Upload documents larger than 5MB

---

## Sample Document Mapping

Use these sample documents for testing:

| Requirement | Sample Document | Expected Result |
|-------------|----------------|-----------------|
| AC-1: Password policy | `password_policy.txt` | ✅ 90%+ confidence |
| IM-1: Incident response plan | `incident_response_plan.txt` | ✅ 85%+ confidence |
| DP-1: Backup policy | `backup_policy.txt` | ✅ 88%+ confidence |

**Note:** The other 7 requirements don't have sample documents. In a real scenario, you'd upload your own organizational documents.

---

## Troubleshooting

### "Error analyzing document"
**Cause:** OpenAI API issue or network problem
**Fix:** Check browser console, verify API key is valid

### "Analyzing..." never finishes
**Cause:** Backend service not responding
**Fix:** Check Docker containers are running: `docker-compose ps`

### Low confidence score on matching document
**Cause:** Document may be missing specific details
**Fix:** Review "Missing Elements" and update document

### Progress not updating
**Cause:** Browser cache or state issue
**Fix:** Refresh the page (F5)

---

## Understanding the Compliance Flow

```
1. Organization has compliance requirements
   ↓
2. Upload evidence documents
   ↓
3. AI analyzes if document satisfies requirement
   ↓
4. System calculates confidence score
   ↓
5. Requirement status updates (Pending → Partial → Completed)
   ↓
6. Overall compliance percentage increases
   ↓
7. Gap analysis shows what's still missing
   ↓
8. Continue until 100% compliance achieved
```

---

## Real-World Usage

In a production environment, you would:

1. **Create checklists** for your compliance frameworks (ISO 27001, SOC 2, GDPR, etc.)
2. **Upload your actual policies and procedures** as evidence
3. **Review AI analysis** to ensure documents are complete
4. **Update documents** based on gap analysis
5. **Generate reports** for auditors
6. **Track compliance over time** as policies change

---

## Next Steps After Testing

Once you've uploaded all 3 sample documents (30% completion):

1. **Review the Gap Analysis** section
2. **Check the recommendations**
3. **Notice which requirements** still need evidence
4. **Understand** that in production, you'd create documents for the remaining requirements

In a real implementation:
- You'd have actual organizational policies
- Multiple team members could upload evidence
- Auditors could review the analysis
- Reports could be exported for compliance audits

---

## Questions?

- Check the main [README.md](./README.md) for technical details
- Review [ARCHITECTURE.md](./ARCHITECTURE.md) for system design
- See [AI_USAGE.md](./AI_USAGE.md) for AI integration details

Happy compliance checking! 🎉
