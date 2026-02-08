import React, { useMemo, useState } from 'react';
import {
  AppBar,
  Box,
  Button,
  Container,
  Divider,
  Grid,
  Paper,
  Step,
  StepLabel,
  Stepper,
  TextField,
  Toolbar,
  Typography,
  Chip,
  LinearProgress,
  List,
  ListItem,
  ListItemText
} from '@mui/material';
import axios from 'axios';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080'
});

const steps = [
  'Select Game Folder',
  'Tools Setup',
  'Subtitles',
  'Mapping',
  'Voice Preview',
  'Build Patch',
  'Apply Patch',
  'Rollback'
];

export default function App() {
  const [activeStep, setActiveStep] = useState(0);
  const [gamePath, setGamePath] = useState('');
  const [scanResult, setScanResult] = useState(null);
  const [toolStatus, setToolStatus] = useState({});
  const [project, setProject] = useState(null);
  const [subtitles, setSubtitles] = useState([]);
  const [mappings, setMappings] = useState([]);
  const [previewText, setPreviewText] = useState('Hello, Wei Shen.');
  const [previewVoice, setPreviewVoice] = useState('neutral_female');
  const [previewAudioUrl, setPreviewAudioUrl] = useState(null);
  const [manifest, setManifest] = useState(null);
  const [applyResult, setApplyResult] = useState(null);
  const [rollbackResult, setRollbackResult] = useState(null);
  const [loading, setLoading] = useState(false);

  const statusChips = useMemo(() => (
    Object.entries(toolStatus).map(([tool, status]) => (
      <Chip key={tool} label={`${tool}: ${status}`} color={status === 'READY' ? 'success' : 'warning'} sx={{ mr: 1, mb: 1 }} />
    ))
  ), [toolStatus]);

  const handleScan = async () => {
    setLoading(true);
    try {
      const response = await api.post('/api/installations/scan', { path: gamePath });
      setScanResult(response.data);
    } finally {
      setLoading(false);
    }
  };

  const handleToolStatus = async () => {
    const response = await api.get('/api/tools/status');
    setToolStatus(response.data);
  };

  const handleCreateProject = async () => {
    const response = await api.post('/api/projects', { name: 'SDDE Steam Project', gamePath });
    setProject(response.data);
  };

  const handleExtractSubtitles = async () => {
    if (!project) return;
    const response = await api.post(`/api/projects/${project.id}/extract-subtitles`);
    setSubtitles(response.data);
  };

  const handleAutoMapping = async () => {
    if (!project) return;
    const response = await api.post(`/api/projects/${project.id}/mapping/auto`);
    setMappings(response.data);
  };

  const handlePreview = async () => {
    if (!project) return;
    const response = await api.post(
      `/api/projects/${project.id}/tts/preview`,
      { text: previewText, voice: previewVoice },
      { responseType: 'blob' }
    );
    const url = URL.createObjectURL(response.data);
    setPreviewAudioUrl(url);
  };

  const handleBuildPatch = async () => {
    if (!project) return;
    const response = await api.post(`/api/projects/${project.id}/build-patch`);
    setManifest(response.data);
  };

  const handleApplyPatch = async () => {
    if (!project || !manifest) return;
    const response = await api.post(`/api/projects/${project.id}/apply-patch`, manifest);
    setApplyResult(response.data);
  };

  const handleRollback = async () => {
    if (!project || !manifest) return;
    const response = await api.post(`/api/projects/${project.id}/rollback`, manifest);
    setRollbackResult(response.data);
  };

  return (
    <Box sx={{ bgcolor: '#0b0f1a', minHeight: '100vh', color: 'white' }}>
      <AppBar position="static" color="transparent" elevation={0}>
        <Toolbar>
          <Typography variant="h6">SDDE NeuroDub Studio</Typography>
        </Toolbar>
      </AppBar>
      <Container sx={{ py: 4 }}>
        <Paper sx={{ p: 3, mb: 3, bgcolor: '#121826', color: 'white' }}>
          <Typography variant="h5" sx={{ mb: 2 }}>Pipeline Wizard</Typography>
          <Stepper activeStep={activeStep} alternativeLabel sx={{ mb: 2 }}>
            {steps.map((label) => (
              <Step key={label}>
                <StepLabel sx={{ color: 'white' }}>{label}</StepLabel>
              </Step>
            ))}
          </Stepper>
          {loading && <LinearProgress sx={{ mb: 2 }} />}
          <Grid container spacing={2}>
            <Grid item xs={12} md={6}>
              <Paper sx={{ p: 2, bgcolor: '#1a2233', color: 'white' }}>
                <Typography variant="subtitle1">1. Select Game Folder</Typography>
                <TextField
                  fullWidth
                  variant="outlined"
                  label="Game Path"
                  value={gamePath}
                  onChange={(event) => setGamePath(event.target.value)}
                  sx={{ mt: 2, input: { color: 'white' }, label: { color: '#aab' } }}
                />
                <Button variant="contained" sx={{ mt: 2 }} onClick={handleScan}>Scan</Button>
                <Button variant="outlined" sx={{ mt: 2, ml: 2 }} onClick={handleCreateProject}>Create Project</Button>
                {scanResult && (
                  <>
                    {scanResult.warning && (
                      <Typography variant="body2" sx={{ mt: 2, color: '#f9c74f' }}>
                        {scanResult.warning}
                      </Typography>
                    )}
                    <List dense>
                      {scanResult.files.map((file) => (
                        <ListItem key={file.path}>
                          <ListItemText primary={file.path} secondary={file.exists ? 'OK' : 'Missing'} />
                        </ListItem>
                      ))}
                    </List>
                  </>
                )}
              </Paper>
            </Grid>
            <Grid item xs={12} md={6}>
              <Paper sx={{ p: 2, bgcolor: '#1a2233', color: 'white' }}>
                <Typography variant="subtitle1">2. Tools Setup</Typography>
                <Button variant="outlined" sx={{ mt: 2 }} onClick={handleToolStatus}>Check Tools</Button>
                <Box sx={{ mt: 2, display: 'flex', flexWrap: 'wrap' }}>
                  {statusChips}
                </Box>
              </Paper>
            </Grid>
            <Grid item xs={12} md={6}>
              <Paper sx={{ p: 2, bgcolor: '#1a2233', color: 'white' }}>
                <Typography variant="subtitle1">3. Subtitles Extract</Typography>
                <Button variant="contained" sx={{ mt: 2 }} onClick={handleExtractSubtitles}>Extract Sample</Button>
                <Typography variant="body2" sx={{ mt: 1 }}>Loaded: {subtitles.length}</Typography>
              </Paper>
            </Grid>
            <Grid item xs={12} md={6}>
              <Paper sx={{ p: 2, bgcolor: '#1a2233', color: 'white' }}>
                <Typography variant="subtitle1">4. Mapping</Typography>
                <Button variant="contained" sx={{ mt: 2 }} onClick={handleAutoMapping}>Auto Map</Button>
                <Typography variant="body2" sx={{ mt: 1 }}>Mappings: {mappings.length}</Typography>
              </Paper>
            </Grid>
            <Grid item xs={12} md={6}>
              <Paper sx={{ p: 2, bgcolor: '#1a2233', color: 'white' }}>
                <Typography variant="subtitle1">5. Voice Preview</Typography>
                <TextField
                  fullWidth
                  variant="outlined"
                  label="Preview Text"
                  value={previewText}
                  onChange={(event) => setPreviewText(event.target.value)}
                  sx={{ mt: 2, input: { color: 'white' }, label: { color: '#aab' } }}
                />
                <TextField
                  fullWidth
                  variant="outlined"
                  label="Voice"
                  value={previewVoice}
                  onChange={(event) => setPreviewVoice(event.target.value)}
                  sx={{ mt: 2, input: { color: 'white' }, label: { color: '#aab' } }}
                />
                <Button variant="contained" sx={{ mt: 2 }} onClick={handlePreview}>Generate Preview</Button>
                {previewAudioUrl && (
                  <Box sx={{ mt: 2 }}>
                    <audio controls src={previewAudioUrl} />
                  </Box>
                )}
              </Paper>
            </Grid>
            <Grid item xs={12} md={6}>
              <Paper sx={{ p: 2, bgcolor: '#1a2233', color: 'white' }}>
                <Typography variant="subtitle1">6. Build Patch</Typography>
                <Button variant="contained" sx={{ mt: 2 }} onClick={handleBuildPatch}>Build Patch</Button>
                {manifest && (
                  <Typography variant="body2" sx={{ mt: 1 }}>Patch: {manifest.patchId}</Typography>
                )}
              </Paper>
            </Grid>
            <Grid item xs={12} md={6}>
              <Paper sx={{ p: 2, bgcolor: '#1a2233', color: 'white' }}>
                <Typography variant="subtitle1">7. Apply Patch</Typography>
                <Button variant="contained" sx={{ mt: 2 }} onClick={handleApplyPatch}>Apply</Button>
                {applyResult && (
                  <Typography variant="body2" sx={{ mt: 1 }}>{applyResult.message}</Typography>
                )}
              </Paper>
            </Grid>
            <Grid item xs={12} md={6}>
              <Paper sx={{ p: 2, bgcolor: '#1a2233', color: 'white' }}>
                <Typography variant="subtitle1">8. Rollback</Typography>
                <Button variant="outlined" sx={{ mt: 2 }} onClick={handleRollback}>Rollback</Button>
                {rollbackResult && (
                  <Typography variant="body2" sx={{ mt: 1 }}>{rollbackResult.message}</Typography>
                )}
              </Paper>
            </Grid>
          </Grid>
          <Divider sx={{ my: 3 }} />
          <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
            <Button variant="text" onClick={() => setActiveStep(Math.max(activeStep - 1, 0))}>Back</Button>
            <Button variant="text" onClick={() => setActiveStep(Math.min(activeStep + 1, steps.length - 1))}>Next</Button>
          </Box>
        </Paper>
      </Container>
    </Box>
  );
}
