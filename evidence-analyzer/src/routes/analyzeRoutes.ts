import { Router, Request, Response } from 'express';
import multer from 'multer';
import { AIService } from '../services/aiService';
import { DocumentMatchRequest, GapAnalysisRequest } from '../types';

const router = Router();
const aiService = new AIService();

// Configure multer for file uploads
const storage = multer.memoryStorage();
const upload = multer({
  storage,
  limits: { fileSize: 5 * 1024 * 1024 }, // 5MB limit
});

// POST /api/analyze/document - Upload and analyze document
router.post('/document', upload.single('document'), async (req: Request, res: Response) => {
  try {
    if (!req.file) {
      res.status(400).json({ error: 'No document uploaded' });
      return;
    }

    const { requirement, hints } = req.body;

    if (!requirement) {
      res.status(400).json({ error: 'Requirement is required' });
      return;
    }

    const documentText = req.file.buffer.toString('utf-8');
    const hintsArray = hints ? JSON.parse(hints) : [];

    const request: DocumentMatchRequest = {
      documentText,
      requirement,
      hints: hintsArray,
    };

    const result = await aiService.analyzeDocumentMatch(request);

    res.json({
      success: true,
      documentName: req.file.originalname,
      analysis: result,
    });
  } catch (error) {
    console.error('Document analysis error:', error);
    res.status(500).json({
      error: 'Failed to analyze document',
      details: error instanceof Error ? error.message : 'Unknown error'
    });
  }
});

// POST /api/analyze/match - Match document text to requirement
router.post('/match', async (req: Request, res: Response) => {
  try {
    const { documentText, requirement, hints } = req.body;

    if (!documentText || !requirement) {
      res.status(400).json({ error: 'documentText and requirement are required' });
      return;
    }

    const request: DocumentMatchRequest = {
      documentText,
      requirement,
      hints: hints || [],
    };

    const result = await aiService.analyzeDocumentMatch(request);
    res.json(result);
  } catch (error) {
    console.error('Match analysis error:', error);
    res.status(500).json({
      error: 'Failed to match document',
      details: error instanceof Error ? error.message : 'Unknown error'
    });
  }
});

// POST /api/analyze/gaps - Identify compliance gaps
router.post('/gaps', async (req: Request, res: Response) => {
  try {
    const { requirements, evidenceList } = req.body;

    if (!requirements || !Array.isArray(requirements)) {
      res.status(400).json({ error: 'requirements array is required' });
      return;
    }

    const request: GapAnalysisRequest = {
      requirements,
      evidenceList: evidenceList || [],
    };

    const result = await aiService.performGapAnalysis(request);
    res.json(result);
  } catch (error) {
    console.error('Gap analysis error:', error);
    res.status(500).json({
      error: 'Failed to perform gap analysis',
      details: error instanceof Error ? error.message : 'Unknown error'
    });
  }
});

export default router;
