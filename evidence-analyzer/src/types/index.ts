export interface AnalysisResult {
  matches: boolean;
  confidence: number;
  relevant_sections: string[];
  reasoning: string;
  missing_elements: string[];
}

export interface DocumentMatchRequest {
  documentText: string;
  requirement: string;
  hints: string[];
}

export interface GapAnalysisRequest {
  requirements: Array<{
    id: string;
    requirement: string;
    status: string;
  }>;
  evidenceList: Array<{
    documentName: string;
    requirement: string;
  }>;
}

export interface GapAnalysisResult {
  uncoveredRequirements: string[];
  partiallyCovered: Array<{
    requirement: string;
    reason: string;
  }>;
  criticalGaps: string[];
  suggestions: string[];
}
