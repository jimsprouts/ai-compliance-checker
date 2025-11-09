import express from 'express';
import cors from 'cors';
import dotenv from 'dotenv';
import analyzeRoutes from './routes/analyzeRoutes';

dotenv.config();

const app = express();
const PORT = process.env.PORT || 3001;

// Middleware
app.use(cors());
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// Routes
app.use('/api/analyze', analyzeRoutes);

// Health check
app.get('/health', (req, res) => {
  res.json({ status: 'ok', service: 'evidence-analyzer' });
});

// Start server
app.listen(PORT, () => {
  console.log(`Evidence Analyzer Service running on port ${PORT}`);
  console.log(`Health check: http://localhost:${PORT}/health`);
});

export default app;
