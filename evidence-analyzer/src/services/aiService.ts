import OpenAI from 'openai';
import { AnalysisResult, DocumentMatchRequest, GapAnalysisRequest, GapAnalysisResult } from '../types';

const openai = new OpenAI({
  apiKey: process.env.OPENAI_API_KEY || '',
});

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
      const result = JSON.parse(content);

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
3. Critical gaps (most important missing items)
4. Suggested next steps (specific recommendations)

Return ONLY a JSON object with this exact structure:
{
  "uncoveredRequirements": array of requirement IDs,
  "partiallyCovered": array of objects with {requirement, reason},
  "criticalGaps": array of requirement descriptions,
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
      const result = JSON.parse(content);

      return {
        uncoveredRequirements: result.uncoveredRequirements || [],
        partiallyCovered: result.partiallyCovered || [],
        criticalGaps: result.criticalGaps || [],
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
