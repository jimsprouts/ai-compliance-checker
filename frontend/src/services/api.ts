import axios from 'axios';
import type { Checklist, ProgressResponse, AnalysisResult, GapReport } from '../types';

const CHECKLIST_API = import.meta.env.VITE_CHECKLIST_API_URL || 'http://localhost:8080/api';
const ANALYZER_API = import.meta.env.VITE_ANALYZER_API_URL || 'http://localhost:3001/api';
const REPORT_API = import.meta.env.VITE_REPORT_API_URL || 'http://localhost:5001/api';

export const api = {
  // Checklist Service
  getChecklists: async (): Promise<Checklist[]> => {
    const response = await axios.get(`${CHECKLIST_API}/checklists`);
    return response.data;
  },

  getChecklist: async (id: string): Promise<Checklist> => {
    const response = await axios.get(`${CHECKLIST_API}/checklists/${id}`);
    return response.data;
  },

  updateItemStatus: async (
    checklistId: string,
    itemId: string,
    status: string,
    evidence?: any
  ) => {
    const response = await axios.post(
      `${CHECKLIST_API}/checklists/${checklistId}/items/${itemId}/status`,
      { status, evidence }
    );
    return response.data;
  },

  getProgress: async (checklistId: string): Promise<ProgressResponse> => {
    const response = await axios.get(`${CHECKLIST_API}/checklists/${checklistId}/progress`);
    return response.data;
  },

  // Evidence Analyzer Service
  analyzeDocument: async (
    file: File,
    requirement: string,
    hints: string[]
  ): Promise<{ success: boolean; documentName: string; analysis: AnalysisResult }> => {
    const formData = new FormData();
    formData.append('document', file);
    formData.append('requirement', requirement);
    formData.append('hints', JSON.stringify(hints));

    const response = await axios.post(`${ANALYZER_API}/analyze/document`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
    return response.data;
  },

  matchDocument: async (
    documentText: string,
    requirement: string,
    hints: string[]
  ): Promise<AnalysisResult> => {
    const response = await axios.post(`${ANALYZER_API}/analyze/match`, {
      documentText,
      requirement,
      hints,
    });
    return response.data;
  },

  // Report Service
  getGapReport: async (checklistId: string): Promise<GapReport> => {
    const response = await axios.get(`${REPORT_API}/report/gaps/${checklistId}`);
    return response.data;
  },
};
