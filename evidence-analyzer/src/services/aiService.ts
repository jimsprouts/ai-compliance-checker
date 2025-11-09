import OpenAI from 'openai';
import { AnalysisResult, DocumentMatchRequest, GapAnalysisRequest, GapAnalysisResult } from '../types';

const openai = new OpenAI({
  apiKey: process.env.OPENAI_API_KEY || '',
});

// Helper function to clean markdown code blocks from JSON responses
function cleanJsonResponse(content: string): string {
  // Remove markdown code blocks (```json ... ``` or ``` ... ```)
  let cleaned = content.trim();
  cleaned = cleaned.replace(/^```json?\s*/i, '');
  cleaned = cleaned.replace(/\s*```\s*$/i, '');
  return cleaned.trim();
}

export class AIService {
  async analyzeDocumentMatch(request: DocumentMatchRequest): Promise<AnalysisResult> {
    const prompt = `
Analyze if this document provides evidence for the compliance requirement.

DOCUMENT CONTENT:
${request.documentText}

REQUIREMENT:
${request.requirement}

HINTS FOR MATCHING:
${request.hints.join(', ')}

CONFIDENCE SCORING GUIDELINES:
- 0.9-1.0: Comprehensive, detailed evidence that fully addresses all aspects
- 0.7-0.9: Good evidence with most requirements covered
- 0.4-0.7: Partial evidence - mentions topic but lacks important details
- 0.1-0.4: Minimal evidence - brief mention without substance or specifics
- 0.0-0.1: No relevant evidence - completely wrong topic or no mention at all

IMPORTANT:
- If document mentions the topic but is very brief/vague, score 0.1-0.3 (not 0)
- Only score 0.0 if document is completely unrelated
- "matches" = true if confidence >= 0.1 (any mention), false if < 0.1 (wrong topic)

Return ONLY a JSON object with this exact structure (no additional text):
{
  "matches": boolean,
  "confidence": number between 0.0 and 1.0,
  "relevant_sections": array of relevant quotes (max 2),
  "reasoning": brief explanation,
  "missing_elements": array of what's still needed
}`;

    try {
      const response = await openai.chat.completions.create({
        model: process.env.OPENAI_MODEL || 'gpt-3.5-turbo',
        messages: [
          {
            role: 'system',
            content: 'You are a compliance analysis expert. Always respond with valid JSON only.',
          },
          {
            role: 'user',
            content: prompt,
          },
        ],
        max_completion_tokens: 500,
      });

      const content = response.choices[0]?.message?.content || '{}';
      const cleaned = cleanJsonResponse(content);
      const result = JSON.parse(cleaned);

      return {
        matches: result.matches || false,
        confidence: result.confidence || 0,
        relevant_sections: result.relevant_sections || [],
        reasoning: result.reasoning || 'No analysis available',
        missing_elements: result.missing_elements || [],
      };
    } catch (error) {
      console.error('AI analysis error:', error);
      // Return fallback result
      return {
        matches: false,
        confidence: 0,
        relevant_sections: [],
        reasoning: 'Error during AI analysis',
        missing_elements: ['Unable to analyze document'],
      };
    }
  }

  async performGapAnalysis(request: GapAnalysisRequest): Promise<GapAnalysisResult> {
    const prompt = `
Based on these compliance requirements and current evidence:

REQUIREMENTS: ${JSON.stringify(request.requirements, null, 2)}
EVIDENCE PROVIDED: ${JSON.stringify(request.evidenceList, null, 2)}

Identify:
1. Uncovered requirements (no evidence at all)
2. Partially covered items (some evidence but incomplete)
3. Critical gaps (most important missing items from security and compliance perspective)
4. Suggested next steps (specific recommendations for each gap)

IMPORTANT for criticalGaps:
- MUST include the requirement ID and description in EXACT format "ID: Description"
- Example: "AC-1: Password policy documented and enforced"
- Example: "RM-1: Risk assessment conducted annually"
- Prioritize security and compliance critical items from ALL categories:
  * Access controls (password policies, user reviews, admin monitoring)
  * Data protection (backups, encryption, retention)
  * Risk management (risk assessments) - VERY IMPORTANT
  * Incident management (response plans, logging)
- Include ALL PENDING items from these critical categories

Return ONLY a JSON object with this exact structure:
{
  "uncoveredRequirements": array of requirement IDs,
  "partiallyCovered": array of objects with {requirement, reason},
  "criticalGaps": array of strings in EXACT format "ID: Description" (use the ID from the requirements array),
  "suggestions": array of specific next steps
}`;

    try {
      const response = await openai.chat.completions.create({
        model: process.env.OPENAI_MODEL || 'gpt-3.5-turbo',
        messages: [
          {
            role: 'system',
            content: 'You are a compliance gap analysis expert. Always respond with valid JSON only.',
          },
          {
            role: 'user',
            content: prompt,
          },
        ],
        max_completion_tokens: 800,
      });

      const content = response.choices[0]?.message?.content || '{}';
      const cleaned = cleanJsonResponse(content);
      const result = JSON.parse(cleaned);

      // Post-process critical gaps to ensure they have IDs
      let criticalGaps = result.criticalGaps || [];
      if (criticalGaps.length > 0) {
        criticalGaps = criticalGaps.map((gap: string) => {
          // If gap doesn't start with an ID format (e.g., "AC-1:"), try to match it to a requirement
          if (!/^[A-Z]+-\d+:/.test(gap)) {
            // Find matching requirement
            const matchingReq = request.requirements.find(req =>
              req.requirement.toLowerCase() === gap.toLowerCase() ||
              gap.toLowerCase().includes(req.requirement.toLowerCase()) ||
              req.requirement.toLowerCase().includes(gap.toLowerCase())
            );
            if (matchingReq) {
              return `${matchingReq.id}: ${matchingReq.requirement}`;
            }
          }
          return gap;
        });
      }

      return {
        uncoveredRequirements: result.uncoveredRequirements || [],
        partiallyCovered: result.partiallyCovered || [],
        criticalGaps: criticalGaps,
        suggestions: result.suggestions || [],
      };
    } catch (error) {
      console.error('Gap analysis error:', error);
      return {
        uncoveredRequirements: [],
        partiallyCovered: [],
        criticalGaps: [],
        suggestions: ['Unable to perform gap analysis due to error'],
      };
    }
  }
}
