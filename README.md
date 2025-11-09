# Compliance Checker - AI-Powered Compliance Management System

A microservices-based compliance checking system that helps organizations track and verify ISO 27001 compliance requirements using AI-powered document analysis.

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

## Architecture Overview

The system consists of 4 microservices:

1. **Checklist Service** (Java/Spring Boot - Port 8080) - Manages compliance checklists
2. **Evidence Analyzer** (TypeScript/Node.js - Port 3001) - AI-powered document analysis
3. **Report Generator** (Java/Spring Boot - Port 5001) - Generates compliance reports with AI recommendations
4. **Frontend** (React + TypeScript - Port 3000) - User interface

## Usage

### 1. View Compliance Checklist

When you open the dashboard, you'll see:
- Overall compliance progress (percentage)
- List of all compliance requirements
- Status for each requirement (Pending/Partial/Completed)

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
- Critical gaps requiring immediate attention
- AI-generated recommendations that change based on uploaded evidence

### 4. Track Progress

The progress bar updates in real-time as you upload evidence and complete requirements.

## Sample Documents

Sample compliance documents are provided in `sample-documents/`:

### Complete Evidence (Strong)
- **AC-1_password_policy.txt** - Comprehensive password policy
- **AC-2_access_review_procedure.txt** - Access review procedures
- **AC-3_admin_access_logging.txt** - Administrative access logging
- **IM-1_incident_response_plan.txt** - Incident response plan
- **IM-2_incident_log_template.txt** - Incident logging template
- **IM-3_disaster_recovery_testing.txt** - DR testing procedures
- **DP-1_backup_policy.txt** - Backup and recovery policy
- **DP-2_encryption_standards.txt** - Encryption standards
- **DP-3_data_retention_policy.txt** - Data retention policy
- **RM-1_risk_assessment_report.txt** - Risk assessment report

### Weak Evidence (Gap Testing)
In `sample-documents/weak-evidence/`:
- **WEAK_AC-1_password_policy.txt** - Minimal password policy
- **WEAK_AC-2_access_review.txt** - Vague access review notes
- **WEAK_DP-2_encryption.txt** - Brief encryption mention

### Wrong Evidence (Mismatch Testing)
In `sample-documents/wrong-evidence/`:
- **WRONG_vacation_policy.txt** - Vacation policy (unrelated)
- **WRONG_employee_handbook.txt** - HR handbook (unrelated)

## Service Endpoints

### Checklist Service (Port 8080)
- `GET /api/checklists` - List all checklists
- `GET /api/checklists/{id}` - Get specific checklist
- `POST /api/checklists/{id}/items/{itemId}/status` - Update item status
- `GET /api/checklists/{id}/progress` - Get compliance progress

### Evidence Analyzer (Port 3001)
- `POST /api/analyze/document` - Upload and analyze document
- `POST /api/analyze/match` - Match document text to requirement

### Report Generator (Port 5001)
- `GET /api/report/compliance/{checklistId}` - Full compliance report
- `GET /api/report/gaps/{checklistId}` - Gap analysis report
- `POST /api/report/suggestions` - Get AI improvement suggestions

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

### Running Test Scripts

```bash
# Test all endpoints
./test-endpoints.sh

# Test status logic
./test-status-logic.sh

# Comprehensive test suite
./test-comprehensive.sh
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

### Port Conflicts

If ports are already in use, modify `docker-compose.yml`:
```yaml
ports:
  - "8081:8080"  # Change left side (host port)
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

### Infrastructure
- **Docker** for containerization
- **Docker Compose** for orchestration

## Additional Documentation

- [ARCHITECTURE.md](./ARCHITECTURE.md) - System design and architecture decisions
- [AI_USAGE.md](./AI_USAGE.md) - AI tools usage, prompts, and limitations
