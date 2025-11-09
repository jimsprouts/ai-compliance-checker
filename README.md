# Compliance Checker - AI-Powered Compliance Management System

A microservices-based compliance checking system that helps organizations track and verify ISO 27001 compliance requirements using AI-powered document analysis.

## Features

- **Checklist Management**: Track compliance requirements across multiple categories (Access Control, Incident Management, Data Protection, Risk Management)
- **AI-Powered Document Analysis**: Upload compliance evidence and get instant AI analysis with confidence scores
- **Smart Status Logic**: Status determined by **best evidence**, not latest upload - once you upload strong evidence (>70% confidence), status won't degrade
- **Gap Detection**: Automatically identifies weak evidence (<30% confidence) and missing elements
- **Gap Analysis**: Automatically identify compliance gaps and get recommendations
- **Progress Tracking**: Real-time compliance progress visualization
- **Interactive Dashboard**: Modern React-based UI for managing compliance workflows

## Architecture

### Microservices

1. **Checklist Service** (Java/Spring Boot - Port 8080)
   - Manages compliance checklists and items
   - Tracks completion status
   - Stores evidence references

2. **Evidence Analyzer** (TypeScript/Node.js/Express - Port 3001)
   - AI-powered document analysis
   - Matches documents to requirements
   - Provides confidence scores and reasoning

3. **Report Generator** (Java/Spring Boot - Port 5001)
   - Generates compliance reports
   - Performs gap analysis
   - Provides improvement suggestions

4. **Frontend** (React + TypeScript - Port 3000)
   - User interface for all operations
   - Document upload with drag & drop
   - Real-time progress visualization

## Prerequisites

- Docker and Docker Compose
- OpenAI API Key (for AI analysis features)
- At least 4GB RAM available for Docker

## Quick Start

### 1. Clone the Repository

```bash
git clone <repository-url>
cd compliance-checker
```

### 2. Configure Environment Variables

Create a `.env` file in the root directory:

```bash
cp .env.example .env
```

Edit `.env` and add your OpenAI API key:

```env
OPENAI_API_KEY=your_actual_api_key_here
OPENAI_MODEL=gpt-3.5-turbo
```

### 3. Start All Services

```bash
docker-compose up --build
```

This command will:
- Build all Docker images
- Start all microservices
- Set up networking between services
- Make the application available at http://localhost:3000

### 4. Access the Application

Open your browser and navigate to:
```
http://localhost:3000
```

## Service Endpoints

### Checklist Service (Port 8080)
- `GET /api/checklists` - List all checklists
- `GET /api/checklists/{id}` - Get specific checklist
- `POST /api/checklists/{id}/items/{itemId}/status` - Update item status
- `GET /api/checklists/{id}/progress` - Get compliance progress

### Evidence Analyzer (Port 3001)
- `POST /api/analyze/document` - Upload and analyze document
- `POST /api/analyze/match` - Match document text to requirement
- `POST /api/analyze/gaps` - Identify compliance gaps

### Report Generator (Port 5001)
- `GET /api/report/compliance/{checklistId}` - Full compliance report
- `GET /api/report/gaps/{checklistId}` - Gap analysis report
- `POST /api/report/suggestions` - Get AI improvement suggestions

## Usage Guide

### 1. View Compliance Checklist

When you open the dashboard, you'll see:
- Overall compliance progress (percentage)
- Total, completed, partial, and pending items
- List of all compliance requirements

### 2. Upload Evidence

1. Click "Upload Evidence" on any requirement
2. Select a document (TXT, PDF, DOC, DOCX)
3. Click "Analyze & Upload"
4. Review AI analysis results:
   - Match status (Yes/No)
   - Confidence score (0-100%)
   - Reasoning
   - Missing elements (if any)

### 3. View Gap Analysis

The Gap Analysis section shows:
- Total number of gaps
- Critical gaps requiring immediate attention
- Recommendations for improvement

### 4. Track Progress

The progress bar and statistics update in real-time as you:
- Upload evidence
- Complete requirements
- Update item statuses

## How Status is Determined (IMPORTANT!)

The system uses **BEST EVIDENCE** logic:

### Status Formula
```
Status = based on HIGHEST confidence among ALL evidence

Confidence > 70%  → COMPLETED ✅
Confidence > 30%  → PARTIAL ⚠️
Confidence ≤ 30%  → PENDING ❌
```

### Key Behavior

✅ **Once you upload strong evidence (>70%), status won't degrade**

Example:
1. Upload `AC-1_password_policy.txt` (95% confidence) → Status: COMPLETED ✅
2. Upload `WEAK_AC-1_password_policy.txt` (10% confidence) → Status: **STILL COMPLETED** ✅

**Why?** The system looks at ALL evidence and uses the BEST one. Even though you uploaded weak evidence, there's still strong evidence (95%) in the list.

📖 **See [STATUS_LOGIC_EXPLAINED.md](STATUS_LOGIC_EXPLAINED.md) for detailed examples and testing scenarios.**

---

## Sample Documents

### Strong Evidence (Complete Compliance)

Ten comprehensive sample compliance documents are provided in `sample-documents/` to test all ISO 27001 requirements:

### Access Control (AC)
1. **AC-1_password_policy.txt** - Password policy with complexity requirements
2. **AC-2_access_review_procedure.txt** - Quarterly access review procedures
3. **AC-3_admin_access_logging.txt** - Administrative access logging policy

### Incident Management (IM)
4. **IM-1_incident_response_plan.txt** - Complete incident response procedures
5. **IM-2_incident_log_template.txt** - Security incident logging template
6. **IM-3_disaster_recovery_testing.txt** - DR testing and procedures

### Data Protection (DP)
7. **DP-1_backup_policy.txt** - Comprehensive backup and recovery policy
8. **DP-2_encryption_standards.txt** - Encryption standards and implementation
9. **DP-3_data_retention_policy.txt** - Data retention and disposal policy

### Risk Management (RM)
10. **RM-1_risk_assessment_report.txt** - Annual risk assessment report

**File naming:** Each file starts with the requirement ID (e.g., `AC-1_`) for easy identification.

---

### Weak Evidence (Gap Detection Testing)

Three weak/incomplete documents in `sample-documents/weak-evidence/` to test gap detection:

1. **WEAK_AC-1_password_policy.txt** - Minimal password policy (lacks complexity, rotation, MFA)
   - **Expected result:** PENDING status, ~10% confidence, missing elements identified
2. **WEAK_AC-2_access_review.txt** - Vague access review notes (no schedule, process)
   - **Expected result:** PENDING status, ~10-15% confidence
3. **WEAK_DP-2_encryption.txt** - Brief encryption mention (no standards, algorithms)
   - **Expected result:** PENDING status, ~10% confidence

**File naming:** Files start with `WEAK_` prefix followed by requirement ID.

**Note:** These documents are intentionally minimal to return <30% confidence (PENDING status).

---

### Wrong Evidence (Mismatch Detection Testing)

Two completely irrelevant documents in `sample-documents/wrong-evidence/` to test rejection:

1. **WRONG_vacation_policy.txt** - Vacation policy (unrelated to security)
   - **Expected result:** PENDING status, <10% confidence, matches: false
2. **WRONG_employee_handbook.txt** - General HR handbook (unrelated to compliance)
   - **Expected result:** PENDING status, <15% confidence, matches: false

**File naming:** Files start with `WRONG_` prefix (can be used for any requirement).

**Purpose:** Prove the AI doesn't accept irrelevant documents!

---

### Testing the System

To achieve 100% compliance:
1. Navigate to the dashboard at http://localhost:3000
2. For each requirement, click "Upload Evidence"
3. Upload the corresponding sample document (file name matches requirement ID!)
4. Review the AI analysis (confidence score, reasoning, gaps)
5. Watch the compliance progress increase to 100%

Example workflow:
- **AC-1:** Password policy → Upload `AC-1_password_policy.txt` ✅
- **AC-2:** Access reviews → Upload `AC-2_access_review_procedure.txt` ✅
- **AC-3:** Admin logging → Upload `AC-3_admin_access_logging.txt` ✅
- Continue for all 10 requirements

**Pro tip:** File names start with the requirement ID, so you always know which file to upload!

## Development

### Running Services Individually

#### Checklist Service
```bash
cd checklist-service
mvn spring-boot:run
```

#### Evidence Analyzer
```bash
cd evidence-analyzer
npm install
npm run dev
```

#### Report Generator
```bash
cd report-generator
mvn spring-boot:run
```

#### Frontend
```bash
cd frontend
npm install
npm run dev
```

### Building Services

#### Java Services
```bash
cd checklist-service  # or report-generator
mvn clean package
```

#### TypeScript Service
```bash
cd evidence-analyzer
npm run build
```

#### Frontend
```bash
cd frontend
npm run build
```

## Technology Stack

### Backend
- **Java 17** with Spring Boot 3.2.1
- **Node.js 20** with TypeScript
- **Express.js** for REST APIs
- **OpenAI API** for AI analysis

### Frontend
- **React 18** with TypeScript
- **Vite** for build tooling
- **Axios** for API calls
- **CSS3** for styling

### Infrastructure
- **Docker** for containerization
- **Docker Compose** for orchestration
- **Maven** for Java builds
- **npm** for Node.js dependencies

## Configuration

### Environment Variables

**Evidence Analyzer:**
```env
PORT=3001
OPENAI_API_KEY=your_key_here
OPENAI_MODEL=gpt-3.5-turbo
```

**Report Generator:**
```env
CHECKLIST_SERVICE_URL=http://checklist-service:8080
```

**Frontend:**
```env
VITE_CHECKLIST_API_URL=http://localhost:8080/api
VITE_ANALYZER_API_URL=http://localhost:3001/api
VITE_REPORT_API_URL=http://localhost:5001/api
```

## Troubleshooting

### Services Not Starting

Check Docker logs:
```bash
docker-compose logs [service-name]
```

### AI Analysis Not Working

1. Verify OpenAI API key is set correctly in `.env`
2. Check evidence-analyzer logs:
   ```bash
   docker-compose logs evidence-analyzer
   ```
3. Ensure API key has sufficient credits

### CORS Errors

All services are configured with CORS enabled for development. If issues persist:
1. Check browser console for specific errors
2. Verify service URLs in frontend `.env`
3. Restart services: `docker-compose restart`

### Port Conflicts

If ports are already in use, modify `docker-compose.yml`:
```yaml
ports:
  - "8081:8080"  # Change left side (host port)
```

## Testing

### Health Checks

Check if services are running:

```bash
# Checklist Service
curl http://localhost:8080/api/checklists

# Evidence Analyzer
curl http://localhost:3001/health

# Report Generator
curl http://localhost:5001/api/report/compliance/iso-27001-simplified
```

### Manual Testing Flow

1. **View Checklist**: Visit http://localhost:3000
2. **Check Progress**: Verify 0% completion initially
3. **Upload Document**: Use sample document for "Password policy"
4. **Verify Analysis**: Check confidence score and reasoning
5. **Check Progress Update**: Verify percentage increased
6. **View Gap Report**: Check recommendations section

## Production Considerations

This is a POC/MVP. For production use, consider:

- [ ] Add authentication and authorization
- [ ] Implement database persistence (PostgreSQL, MongoDB)
- [ ] Add comprehensive error handling
- [ ] Implement rate limiting for AI calls
- [ ] Add logging and monitoring (ELK stack, Prometheus)
- [ ] Set up CI/CD pipeline
- [ ] Add unit and integration tests
- [ ] Implement PDF parsing for uploaded documents
- [ ] Add user management
- [ ] Enable HTTPS/TLS
- [ ] Configure production-grade CORS
- [ ] Implement caching (Redis)
- [ ] Add audit logging

## License

This project is created as a homework assignment for Fluenta.

## Support

For questions or issues:
- Check the [ARCHITECTURE.md](./ARCHITECTURE.md) for design details
- Review [AI_USAGE.md](./AI_USAGE.md) for AI integration details
- Check logs: `docker-compose logs -f [service-name]`

---

**Note**: This is a proof of concept demonstrating microservices architecture and AI integration. Not intended for production use without additional security and operational hardening.
