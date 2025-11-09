# Quick Setup Guide

## Configuration Steps

### Step 1: Root Directory .env File (REQUIRED for Docker Compose)

Create `.env` in the **root directory** (same location as `docker-compose.yml`):

```bash
cd <project_root>
cp .env.example .env
```

Edit the `.env` file and add your OpenAI API key:

```env
# Required: Your OpenAI API Key
OPENAI_API_KEY=sk-your-actual-openai-api-key-here

# Optional: Change model if desired (default is gpt-3.5-turbo)
OPENAI_MODEL=gpt-3.5-turbo
```

**This is the ONLY .env file you need to configure for Docker Compose to work!**

---

### Step 2: Frontend .env File (OPTIONAL - Only for Local Development)

If you want to run the frontend **outside of Docker** during development:

```bash
cd frontend
cp .env.example .env
```

Edit `frontend/.env`:

```env
VITE_CHECKLIST_API_URL=http://localhost:8080/api
VITE_ANALYZER_API_URL=http://localhost:3001/api
VITE_REPORT_API_URL=http://localhost:5000/api
```

**Note:** When running with Docker Compose, you don't need this file. The frontend will use the default values.

---

### Step 3: Evidence Analyzer .env File (OPTIONAL - Only for Local Development)

If you want to run the evidence-analyzer **outside of Docker**:

```bash
cd evidence-analyzer
cp .env.example .env
```

Edit `evidence-analyzer/.env`:

```env
PORT=3001
OPENAI_API_KEY=sk-your-actual-openai-api-key-here
OPENAI_MODEL=gpt-3.5-turbo
```

**Note:** When running with Docker Compose, this file is not needed. Docker Compose uses the root `.env` file.

---

## Summary: What You Actually Need

### For Docker Compose (Recommended):
```
✅ Root directory .env file (with OPENAI_API_KEY)
❌ No other .env files needed
```

### For Local Development (Advanced):
```
✅ Root directory .env (for evidence-analyzer)
✅ frontend/.env (for API URLs)
✅ evidence-analyzer/.env (if running standalone)
```

---

## Complete Startup Instructions

### Option 1: Docker Compose (Recommended)

1. **Configure root .env file:**
   ```bash
   cd <project_root>
   cp .env.example .env
   nano .env  # or use your favorite editor
   ```

2. **Add your OpenAI API key** in the `.env` file:
   ```env
   OPENAI_API_KEY=sk-proj-xxxxxxxxxxxxxxxxxxxxx
   OPENAI_MODEL=gpt-3.5-turbo
   ```

3. **Start all services:**
   ```bash
   docker-compose up --build
   ```

4. **Access the application:**
   ```
   Frontend: http://localhost:3000
   ```

5. **Test the system:**
   - Open http://localhost:3000
   - Click "Upload Evidence" on any requirement
   - Upload a file from `sample-documents/`
   - See the AI analysis results!

---

### Option 2: Local Development (Individual Services)

Only use this if you want to develop/debug individual services outside Docker.

#### Terminal 1 - Checklist Service:
```bash
cd checklist-service
mvn spring-boot:run
# Runs on http://localhost:8080
```

#### Terminal 2 - Evidence Analyzer:
```bash
cd evidence-analyzer
cp .env.example .env
# Edit .env and add OPENAI_API_KEY
npm install
npm run dev
# Runs on http://localhost:3001
```

#### Terminal 3 - Report Generator:
```bash
cd report-generator
mvn spring-boot:run
# Runs on http://localhost:5000
```

#### Terminal 4 - Frontend:
```bash
cd frontend
cp .env.example .env
npm install
npm run dev
# Runs on http://localhost:5173 (Vite default)
```

---

## How to Get an OpenAI API Key

1. Go to https://platform.openai.com/
2. Sign up or log in
3. Navigate to API Keys section: https://platform.openai.com/api-keys
4. Click "Create new secret key"
5. Copy the key (starts with `sk-proj-` or `sk-`)
6. Paste it into your `.env` file

**Note:** You'll need to add billing information and credits to your OpenAI account to use the API.

---

## Verification Steps

After starting with Docker Compose, verify each service:

```bash
# Check all services are running
docker-compose ps

# Test Checklist Service
curl http://localhost:8080/api/checklists

# Test Evidence Analyzer
curl http://localhost:3001/health

# Test Report Generator
curl http://localhost:5000/api/report/compliance/iso-27001-simplified

# Open Frontend
open http://localhost:3000
```

---

## Troubleshooting

### Issue: "OpenAI API key not configured"

**Solution:**
1. Make sure you created `.env` in the root directory
2. Verify the key starts with `sk-`
3. Restart Docker Compose:
   ```bash
   docker-compose down
   docker-compose up --build
   ```

### Issue: Services won't start

**Solution:**
```bash
# Check logs
docker-compose logs evidence-analyzer

# Rebuild everything
docker-compose down
docker-compose up --build
```

### Issue: Frontend can't connect to backend

**Solution:**
1. Check all services are running: `docker-compose ps`
2. Verify ports are not in use by other applications
3. Check browser console for errors

### Issue: AI analysis fails

**Solution:**
1. Verify OpenAI API key is valid
2. Check you have credits on your OpenAI account
3. View logs: `docker-compose logs evidence-analyzer`

---

## File Locations Summary

```
compliance-checker/
├── .env                          ← CREATE THIS (from .env.example)
├── .env.example                  ← Template file
├── docker-compose.yml            ← Uses root .env file
│
├── evidence-analyzer/
│   ├── .env.example             ← Template (optional for local dev)
│   └── .env                     ← Create only for local dev
│
└── frontend/
    ├── .env.example             ← Template (optional for local dev)
    └── .env                     ← Create only for local dev
```

---

## Quick Start (TL;DR)

```bash
# 1. Go to project root
cd <project_root>

# 2. Create .env file
cp .env.example .env

# 3. Edit .env and add your OpenAI API key
nano .env
# Add: OPENAI_API_KEY=sk-your-key-here

# 4. Start everything
docker-compose up --build

# 5. Open browser
open http://localhost:3000

# Done! 🎉
```
