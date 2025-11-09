export interface Evidence {
  documentId: string;
  documentName: string;
  confidence: number;
  uploadedAt: string;
  relevantSections?: string;
}

export interface ChecklistItem {
  id: string;
  category: string;
  requirement: string;
  hints: string[];
  status: 'PENDING' | 'PARTIAL' | 'COMPLETED';
  evidence: Evidence[];
}

export interface Checklist {
  id: string;
  name: string;
  description: string;
  items: ChecklistItem[];
}

export interface ProgressResponse {
  checklistId: string;
  totalItems: number;
  completedItems: number;
  partialItems: number;
  pendingItems: number;
  completionPercentage: number;
}

export interface AnalysisResult {
  matches: boolean;
  confidence: number;
  relevant_sections: string[];
  reasoning: string;
  missing_elements: string[];
}

export interface GapReport {
  checklistId: string;
  generatedAt: string;
  gaps: Array<{
    requirementId: string;
    requirement: string;
    category: string;
    status: string;
    reason: string;
  }>;
  criticalGaps: string[];
  recommendations: string[];
}
