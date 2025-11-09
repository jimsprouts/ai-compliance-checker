# Compliance Checker Architecture

## System Overview

A microservices-based AI-powered compliance checking system that helps organizations track and verify ISO 27001 compliance requirements.

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                     Frontend (React + TS)                    │
│                     Port: 3000                               │
└──────────────┬──────────────┬──────────────┬────────────────┘
               │              │              │
               │              │              │
       ┌───────▼──────┐ ┌────▼─────┐  ┌────▼──────────┐
       │  Checklist   │ │ Evidence │  │    Report     │
       │   Service    │ │ Analyzer │  │  Generator    │
       │ Java/Spring  │ │ Node.js  │  │ Java/Spring   │
       │   Port: 8080 │ │Port: 3001│  │  Port: 5000   │
       └──────┬───────┘ └────┬─────┘  └───────────────┘
              │              │
              │       ┌──────▼───────┐
              │       │   AI APIs    │
              └───────┤ (OpenAI/etc) │
                      └──────────────┘
```

## Service Responsibilities

### 1. Checklist Service (Java Spring Boot)
**Port:** 8080

**Responsibilities:**
- Manage compliance checklists and items
- Track completion status
- Calculate compliance progress
- Store evidence references

**Technology Stack:**
- Java 17+
- Spring Boot 3.x
- In-memory storage (HashMap)
- REST API

**Key Endpoints:**
- `GET /api/checklists` - List all checklists
- `GET /api/checklists/{id}` - Get specific checklist
- `POST /api/checklists/{id}/items/{itemId}/status` - Update item status
- `GET /api/checklists/{id}/progress` - Get compliance percentage

### 2. Evidence Analyzer (TypeScript/Node.js/Express)
**Port:** 3001

**Responsibilities:**
- Analyze uploaded documents using AI
- Match documents to compliance requirements
- Identify coverage gaps
- Provide confidence scores

**Technology Stack:**
- TypeScript
- Node.js + Express
- OpenAI API (or similar)
- Multer for file uploads

**Key Endpoints:**
- `POST /api/analyze/document` - Upload and analyze document
- `POST /api/analyze/match` - Match document to requirement
- `GET /api/analyze/gaps` - Identify missing evidence

### 3. Report Generator (Java Spring Boot)
**Port:** 5000

**Responsibilities:**
- Generate compliance reports
- Aggregate data from other services
- Calculate category-based summaries
- Gap analysis reports
- Improvement suggestions

**Technology Stack:**
- Java 17+
- Spring Boot 3.x
- Spring WebFlux (for HTTP client)
- REST API
- In-memory processing

**Key Endpoints:**
- `GET /api/report/compliance/{checklistId}` - Full compliance report
- `GET /api/report/gaps/{checklistId}` - Gap analysis
- `POST /api/report/suggestions` - AI suggestions for improvements

### 4. Frontend Dashboard (React + TypeScript)
**Port:** 3000

**Responsibilities:**
- User interface for all operations
- Display compliance status
- Document upload interface
- Visualize progress and gaps

**Technology Stack:**
- React 18
- TypeScript
- Vite
- TailwindCSS or Material-UI
- Axios for API calls

**Key Features:**
- Checklist overview with progress bars
- Drag & drop document upload
- AI analysis results display
- Gap analysis visualization

## Data Models

### Checklist
```json
{
  "id": "iso-27001-simplified",
  "name": "ISO 27001 Essential Controls",
  "items": [
    {
      "id": "AC-1",
      "category": "Access Control",
      "requirement": "Password policy documented and enforced",
      "hints": ["password policy", "security guidelines"],
      "status": "pending|completed|partial",
      "evidence": [
        {
          "documentId": "doc-123",
          "documentName": "password_policy.txt",
          "confidence": 0.85,
          "uploadedAt": "2025-01-08T10:00:00Z"
        }
      ]
    }
  ]
}
```

### Analysis Result
```json
{
  "matches": true,
  "confidence": 0.85,
  "relevant_sections": ["Section 3.2: Password Requirements"],
  "reasoning": "Document contains specific password policy rules",
  "missing_elements": ["Multi-factor authentication not mentioned"]
}
```

## AI Integration Strategy

### Use Cases:
1. **Document-Requirement Matching**
   - AI analyzes document content
   - Compares against requirement text
   - Returns confidence score and reasoning

2. **Gap Analysis**
   - AI identifies uncovered requirements
   - Prioritizes critical vs nice-to-have gaps
   - Suggests next steps

3. **Improvement Suggestions**
   - AI recommends missing documentation
   - Suggests best practices
   - Provides template examples

### AI Provider Options:
- OpenAI GPT-4/3.5
- Anthropic Claude
- Google Gemini
- Groq (free tier)

## Design Decisions

### Why Microservices?
- Demonstrates architectural skills
- Different languages showcase versatility
- Services can scale independently
- Clear separation of concerns

### Why In-Memory Storage?
- POC/MVP requirement
- Fast development
- No database setup needed
- Sufficient for demo purposes

### Why Docker Compose?
- Easy local setup
- Single command deployment
- Environment consistency
- Production-like architecture

### Technology Choices:

**Java Spring Boot for Checklist:**
- Robust enterprise framework
- Strong typing
- Great for CRUD operations
- Excellent REST support

**TypeScript/Node.js for Evidence Analyzer:**
- Different from Java (requirement met)
- Fast async I/O for AI calls
- Rich npm ecosystem
- Excellent for API integrations
- OpenAI SDK support

**Java Spring Boot for Report Generator:**
- Consistent technology with Checklist Service
- Demonstrates Spring WebFlux for service-to-service calls
- Strong typing and compile-time safety
- Excellent for data aggregation and processing
- Robust enterprise patterns

**React + TypeScript for Frontend:**
- Industry standard
- Type safety
- Rich component ecosystem
- Great developer experience

## Security Considerations (POC Level)

- CORS enabled for development
- API keys via environment variables
- No authentication (POC simplification)
- Input validation on endpoints

## Deployment

```bash
docker-compose up
```

All services start automatically with proper networking.

## Future Enhancements (Not Implemented)

- Real-time updates via WebSockets
- PDF parsing support
- User authentication
- Database persistence
- Comprehensive error handling
- Production logging
- CI/CD pipeline
- Unit/integration tests
