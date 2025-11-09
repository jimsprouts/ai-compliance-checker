import { useState, useEffect } from 'react';
import { api } from './services/api';
import type { Checklist, ChecklistItem, ProgressResponse, GapReport } from './types';
import './App.css';

function App() {
  const [, setChecklists] = useState<Checklist[]>([]);
  const [selectedChecklist, setSelectedChecklist] = useState<Checklist | null>(null);
  const [progress, setProgress] = useState<ProgressResponse | null>(null);
  const [gapReport, setGapReport] = useState<GapReport | null>(null);
  const [selectedItem, setSelectedItem] = useState<ChecklistItem | null>(null);
  const [uploadFile, setUploadFile] = useState<File | null>(null);
  const [analyzing, setAnalyzing] = useState(false);
  const [analysisResult, setAnalysisResult] = useState<any>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadChecklists();
  }, []);

  const loadChecklists = async () => {
    try {
      const data = await api.getChecklists();
      setChecklists(data);
      if (data.length > 0) {
        selectChecklist(data[0].id);
      }
    } catch (error) {
      console.error('Error loading checklists:', error);
    } finally {
      setLoading(false);
    }
  };

  const selectChecklist = async (checklistId: string) => {
    try {
      const checklist = await api.getChecklist(checklistId);
      setSelectedChecklist(checklist);

      const prog = await api.getProgress(checklistId);
      setProgress(prog);

      const gaps = await api.getGapReport(checklistId);
      setGapReport(gaps);
    } catch (error) {
      console.error('Error loading checklist:', error);
    }
  };

  const handleFileUpload = async (item: ChecklistItem) => {
    if (!uploadFile || !selectedChecklist) return;

    setAnalyzing(true);
    setAnalysisResult(null);

    try {
      const result = await api.analyzeDocument(uploadFile, item.requirement, item.hints);
      setAnalysisResult(result);

      // Update item status based on analysis
      const newStatus = result.analysis.matches
        ? result.analysis.confidence > 0.7
          ? 'COMPLETED'
          : 'PARTIAL'
        : 'PENDING';

      const evidence = {
        documentId: Date.now().toString(),
        documentName: uploadFile.name,
        confidence: result.analysis.confidence,
        uploadedAt: new Date().toISOString(),
        relevantSections: result.analysis.relevant_sections.join('; '),
      };

      await api.updateItemStatus(selectedChecklist.id, item.id, newStatus, evidence);

      // Reload checklist and progress
      await selectChecklist(selectedChecklist.id);
      setUploadFile(null);
      setSelectedItem(null);
    } catch (error) {
      console.error('Error analyzing document:', error);
      alert('Error analyzing document. Please check your API configuration.');
    } finally {
      setAnalyzing(false);
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'COMPLETED': return '#22c55e';
      case 'PARTIAL': return '#eab308';
      case 'PENDING': return '#ef4444';
      default: return '#6b7280';
    }
  };

  if (loading) {
    return <div className="app"><div className="loading">Loading checklists...</div></div>;
  }

  return (
    <div className="app">
      <header className="header">
        <h1>Compliance Checker Dashboard</h1>
        <p>AI-Powered Compliance Management System</p>
      </header>

      <div className="container">
        {/* Progress Overview */}
        {progress && (
          <div className="card">
            <h2>{selectedChecklist?.name}</h2>
            <div className="progress-bar">
              <div
                className="progress-fill"
                style={{ width: `${progress.completionPercentage}%` }}
              >
                {progress.completionPercentage.toFixed(1)}%
              </div>
            </div>
            <div className="stats">
              <div className="stat">
                <span className="stat-label">Total</span>
                <span className="stat-value">{progress.totalItems}</span>
              </div>
              <div className="stat">
                <span className="stat-label">Completed</span>
                <span className="stat-value" style={{ color: '#22c55e' }}>
                  {progress.completedItems}
                </span>
              </div>
              <div className="stat">
                <span className="stat-label">Partial</span>
                <span className="stat-value" style={{ color: '#eab308' }}>
                  {progress.partialItems}
                </span>
              </div>
              <div className="stat">
                <span className="stat-label">Pending</span>
                <span className="stat-value" style={{ color: '#ef4444' }}>
                  {progress.pendingItems}
                </span>
              </div>
            </div>
          </div>
        )}

        {/* Checklist Items */}
        {selectedChecklist && (
          <div className="card">
            <h2>Requirements</h2>
            <div className="items-list">
              {selectedChecklist.items.map((item) => (
                <div key={item.id} className="item">
                  <div className="item-header">
                    <div className="item-info">
                      <span
                        className="status-badge"
                        style={{ backgroundColor: getStatusColor(item.status) }}
                      >
                        {item.status}
                      </span>
                      <span className="item-id">{item.id}</span>
                      <span className="item-category">{item.category}</span>
                    </div>
                  </div>
                  <p className="item-requirement">{item.requirement}</p>

                  {item.evidence.length > 0 && (
                    <div className="evidence-list">
                      {item.evidence.map((ev, idx) => (
                        <div key={idx} className="evidence-item">
                          📄 {ev.documentName} ({(ev.confidence * 100).toFixed(0)}% confidence)
                        </div>
                      ))}
                    </div>
                  )}

                  <button
                    className="btn-upload"
                    onClick={() => setSelectedItem(item)}
                  >
                    Upload Evidence
                  </button>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Gap Analysis */}
        {gapReport && gapReport.gaps.length > 0 && (
          <div className="card">
            <h2>Gap Analysis</h2>
            <div className="gap-summary">
              <p><strong>Total Gaps:</strong> {gapReport.gaps.length}</p>
              <p><strong>Critical Gaps:</strong> {gapReport.criticalGaps.length}</p>
            </div>

            {/* Detailed Gap List */}
            <div className="gap-details">
              <h3>Requirements Needing Attention</h3>
              <div className="gap-list">
                {gapReport.gaps.map((gap, idx) => (
                  <div key={idx} className="gap-item">
                    <div className="gap-header">
                      <span className="gap-id">{gap.requirementId}</span>
                      <span
                        className="gap-status"
                        style={{
                          backgroundColor: gap.status === 'PENDING' ? '#ef4444' : '#eab308',
                          color: 'white',
                          padding: '2px 8px',
                          borderRadius: '4px',
                          fontSize: '12px'
                        }}
                      >
                        {gap.status}
                      </span>
                    </div>
                    <p className="gap-requirement">{gap.requirement}</p>
                    <p className="gap-reason"><em>{gap.reason}</em></p>
                  </div>
                ))}
              </div>
            </div>

            {/* Critical Gaps Highlight */}
            {gapReport.criticalGaps.length > 0 && (
              <div className="critical-gaps">
                <h3>⚠️ Critical Gaps (High Priority)</h3>
                <ul>
                  {gapReport.criticalGaps.map((gap, idx) => (
                    <li key={idx} style={{ color: '#dc2626', fontWeight: '500' }}>{gap}</li>
                  ))}
                </ul>
              </div>
            )}

            {gapReport.recommendations.length > 0 && (
              <div className="recommendations">
                <h3>Recommendations</h3>
                <ul>
                  {gapReport.recommendations.map((rec, idx) => (
                    <li key={idx}>{rec}</li>
                  ))}
                </ul>
              </div>
            )}
          </div>
        )}
      </div>

      {/* Upload Modal */}
      {selectedItem && (
        <div className="modal-overlay" onClick={() => setSelectedItem(null)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <h3>Upload Evidence</h3>
            <p className="modal-requirement">{selectedItem.requirement}</p>

            <div className="file-upload">
              <input
                type="file"
                accept=".txt,.pdf,.doc,.docx"
                onChange={(e) => setUploadFile(e.target.files?.[0] || null)}
              />
              {uploadFile && <p className="file-name">Selected: {uploadFile.name}</p>}
            </div>

            {analysisResult && (
              <div className="analysis-result">
                <h4>Analysis Result</h4>
                <p><strong>Matches:</strong> {analysisResult.analysis.matches ? 'Yes' : 'No'}</p>
                <p><strong>Confidence:</strong> {(analysisResult.analysis.confidence * 100).toFixed(1)}%</p>
                <p><strong>Reasoning:</strong> {analysisResult.analysis.reasoning}</p>
                {analysisResult.analysis.missing_elements.length > 0 && (
                  <div>
                    <strong>Missing Elements:</strong>
                    <ul>
                      {analysisResult.analysis.missing_elements.map((el: string, idx: number) => (
                        <li key={idx}>{el}</li>
                      ))}
                    </ul>
                  </div>
                )}
              </div>
            )}

            <div className="modal-actions">
              <button
                className="btn-primary"
                onClick={() => handleFileUpload(selectedItem)}
                disabled={!uploadFile || analyzing}
              >
                {analyzing ? 'Analyzing...' : 'Analyze & Upload'}
              </button>
              <button className="btn-secondary" onClick={() => setSelectedItem(null)}>
                Cancel
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default App;
