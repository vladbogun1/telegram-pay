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
  ListItemText,
  Alert
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
  const [errorMessage, setErrorMessage] = useState('');
  const [appLogs, setAppLogs] = useState([]);
  const [backendLogs, setBackendLogs] = useState([]);

  const statusChips = useMemo(() => (
    Object.entries(toolStatus).map(([tool, status]) => (
      <Chip key={tool} label={`${tool}: ${status}`} color={status === 'READY' ? 'success' : 'warning'} sx={{ mr: 1, mb: 1 }} />
    ))
  ), [toolStatus]);

  const currentStep = useMemo(() => {
    if (!scanResult) return 0;
    if (!project) return 1;
    if (subtitles.length === 0) return 2;
    if (mappings.length === 0) return 3;
    if (!previewAudioUrl) return 4;
    if (!manifest) return 5;
    if (!applyResult) return 6;
    return 7;
  }, [scanResult, project, subtitles.length, mappings.length, previewAudioUrl, manifest, applyResult]);

  const pushLog = (message) => {
    const timestamp = new Date().toISOString();
    setAppLogs((prev) => [`${timestamp} ${message}`, ...prev].slice(0, 200));
  };

  const handleScan = async () => {
    setLoading(true);
    setErrorMessage('');
    pushLog(`SCAN start path=${gamePath}`);
    try {
      const response = await api.post('/api/installations/scan', { path: gamePath });
      setScanResult(response.data);
      pushLog(`SCAN success files=${response?.data?.files?.length || 0}`);
    } catch (error) {
      pushLog(`SCAN failed: ${error?.response?.data?.message || error.message}`);
      setErrorMessage(error?.response?.data?.message || 'Scan failed. Check backend logs.');
    } finally {
      setLoading(false);
    }
  };

  const handleToolStatus = async () => {
    setErrorMessage('');
    pushLog('TOOLS status check start');
    try {
      const response = await api.get('/api/tools/status');
      setToolStatus(response.data);
      pushLog(`TOOLS status check success: ${JSON.stringify(response.data)}`);
    } catch (error) {
      pushLog(`TOOLS status check failed: ${error?.response?.data?.message || error.message}`);
      setErrorMessage(error?.response?.data?.message || 'Tool status check failed.');
    }
  };

  const handleCreateProject = async () => {
    setErrorMessage('');
    pushLog('PROJECT create start');
    try {
      const response = await api.post('/api/projects', { name: 'SDDE Steam Project', gamePath });
      setProject(response.data);
      pushLog(`PROJECT create success id=${response?.data?.id}`);
    } catch (error) {
      pushLog(`PROJECT create failed: ${error?.response?.data?.message || error.message}`);
      setErrorMessage(error?.response?.data?.message || 'Project creation failed.');
    }
  };

  const handleExtractSubtitles = async () => {
    if (!project) return;
    setErrorMessage('');
    pushLog(`SUBTITLES extract start project=${project.id}`);
    try {
      const response = await api.post(`/api/projects/${project.id}/extract-subtitles`);
      setSubtitles(response.data);
      pushLog(`SUBTITLES extract success count=${response?.data?.length || 0}`);
    } catch (error) {
      pushLog(`SUBTITLES extract failed: ${error?.response?.data?.message || error.message}`);
      setErrorMessage(error?.response?.data?.message || 'Subtitle extraction failed.');
    }
  };

  const handleAutoMapping = async () => {
    if (!project) return;
    setErrorMessage('');
    pushLog(`MAPPING auto start project=${project.id}`);
    try {
      const response = await api.post(`/api/projects/${project.id}/mapping/auto`);
      setMappings(response.data);
      pushLog(`MAPPING auto success count=${response?.data?.length || 0}`);
    } catch (error) {
      pushLog(`MAPPING auto failed: ${error?.response?.data?.message || error.message}`);
      setErrorMessage(error?.response?.data?.message || 'Auto mapping failed.');
    }
  };

  const handlePreview = async () => {
    if (!project) return;
    setErrorMessage('');
    pushLog(`TTS preview start voice=${previewVoice}`);
    try {
      const response = await api.post(
        `/api/projects/${project.id}/tts/preview`,
        { text: previewText, voice: previewVoice },
        { responseType: 'blob' }
      );
      const url = URL.createObjectURL(response.data);
      setPreviewAudioUrl(url);
      pushLog('TTS preview success');
    } catch (error) {
      pushLog(`TTS preview failed: ${error?.response?.data?.message || error.message}`);
      setErrorMessage(error?.response?.data?.message || 'Preview generation failed.');
    }
  };

  const handleBuildPatch = async () => {
    if (!project) return;
    setErrorMessage('');
    pushLog('PATCH build start');
    try {
      const response = await api.post(`/api/projects/${project.id}/build-patch`);
      setManifest(response.data);
      pushLog(`PATCH build success id=${response?.data?.patchId}`);
    } catch (error) {
      pushLog(`PATCH build failed: ${error?.response?.data?.message || error.message}`);
      setErrorMessage(error?.response?.data?.message || 'Patch build failed.');
    }
  };

  const handleApplyPatch = async () => {
    if (!project || !manifest) return;
    setErrorMessage('');
    pushLog('PATCH apply start');
    try {
      const response = await api.post(`/api/projects/${project.id}/apply-patch`, manifest);
      setApplyResult(response.data);
      pushLog(`PATCH apply success: ${response?.data?.message || 'ok'}`);
    } catch (error) {
      pushLog(`PATCH apply failed: ${error?.response?.data?.message || error.message}`);
      setErrorMessage(error?.response?.data?.message || 'Patch apply failed.');
    }
  };

  const handleRollback = async () => {
    if (!project || !manifest) return;
    setErrorMessage('');
    pushLog('PATCH rollback start');
    try {
      const response = await api.post(`/api/projects/${project.id}/rollback`, manifest);
      setRollbackResult(response.data);
      pushLog(`PATCH rollback success: ${response?.data?.message || 'ok'}`);
    } catch (error) {
      pushLog(`PATCH rollback failed: ${error?.response?.data?.message || error.message}`);
      setErrorMessage(error?.response?.data?.message || 'Rollback failed.');
    }
  };

  const handleLoadBackendLogs = async () => {
    pushLog('SYSTEM logs fetch start');
    try {
      const response = await api.get('/api/system/logs?lines=200');
      setBackendLogs(response.data.lines || []);
      pushLog(`SYSTEM logs fetch success lines=${response?.data?.lines?.length || 0}`);
    } catch (error) {
      pushLog(`SYSTEM logs fetch failed: ${error?.response?.data?.message || error.message}`);
      setErrorMessage(error?.response?.data?.message || 'Failed to load backend logs.');
    }
  };

  const cardStyle = (enabled) => ({
    p: 2,
    bgcolor: '#1a2233',
    color: 'white',
    opacity: enabled ? 1 : 0.75,
    border: enabled ? '1px solid rgba(25,118,210,0.45)' : '1px solid rgba(255,255,255,0.08)'
  });

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
          <Stepper activeStep={currentStep} alternativeLabel sx={{ mb: 2 }}>
            {steps.map((label) => (
              <Step key={label}>
                <StepLabel sx={{ color: 'white' }}>{label}</StepLabel>
              </Step>
            ))}
          </Stepper>
          {loading && <LinearProgress sx={{ mb: 2 }} />}
          {errorMessage && (
            <Typography variant="body2" sx={{ mb: 2, color: '#f94144' }}>
              {errorMessage}
            </Typography>
          )}

          <Grid container spacing={2}>
            <Grid item xs={12} md={6}>
              <Paper sx={cardStyle(true)}>
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
                {scanResult && (
                  <>
                    {scanResult.warning && (
                      <Typography variant="body2" sx={{ mt: 2, color: '#f9c74f' }}>{scanResult.warning}</Typography>
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
              <Paper sx={cardStyle(Boolean(scanResult))}>
                <Typography variant="subtitle1">2. Tools Setup</Typography>
                <Button variant="outlined" sx={{ mt: 2 }} onClick={handleToolStatus}>Check Tools</Button>
                <Button variant="contained" sx={{ mt: 2, ml: 2 }} onClick={handleCreateProject} disabled={!scanResult}>Create Project</Button>
                {!scanResult && <Alert severity="info" sx={{ mt: 2 }}>Run Scan first, then create project.</Alert>}
                <Box sx={{ mt: 2, display: 'flex', flexWrap: 'wrap' }}>{statusChips}</Box>
              </Paper>
            </Grid>

            <Grid item xs={12} md={6}>
              <Paper sx={cardStyle(Boolean(project))}>
                <Typography variant="subtitle1">3. Subtitles Extract</Typography>
                <Button variant="contained" sx={{ mt: 2 }} onClick={handleExtractSubtitles} disabled={!project}>Extract Sample</Button>
                <Typography variant="body2" sx={{ mt: 1 }}>Loaded: {subtitles.length}</Typography>
              </Paper>
            </Grid>

            <Grid item xs={12} md={6}>
              <Paper sx={cardStyle(Boolean(project && subtitles.length > 0))}>
                <Typography variant="subtitle1">4. Mapping</Typography>
                <Button variant="contained" sx={{ mt: 2 }} onClick={handleAutoMapping} disabled={!project || subtitles.length === 0}>Auto Map</Button>
                <Typography variant="body2" sx={{ mt: 1 }}>Mappings: {mappings.length}</Typography>
              </Paper>
            </Grid>

            <Grid item xs={12} md={6}>
              <Paper sx={cardStyle(Boolean(project))}>
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
                <Button variant="contained" sx={{ mt: 2 }} onClick={handlePreview} disabled={!project}>Generate Preview</Button>
                {previewAudioUrl && <Box sx={{ mt: 2 }}><audio controls src={previewAudioUrl} /></Box>}
              </Paper>
            </Grid>

            <Grid item xs={12} md={6}>
              <Paper sx={cardStyle(Boolean(project && mappings.length > 0))}>
                <Typography variant="subtitle1">6. Build Patch</Typography>
                <Button variant="contained" sx={{ mt: 2 }} onClick={handleBuildPatch} disabled={!project || mappings.length === 0}>Build Patch</Button>
                {manifest && <Typography variant="body2" sx={{ mt: 1 }}>Patch: {manifest.patchId}</Typography>}
              </Paper>
            </Grid>

            <Grid item xs={12} md={6}>
              <Paper sx={cardStyle(Boolean(manifest))}>
                <Typography variant="subtitle1">7. Apply Patch</Typography>
                <Button variant="contained" sx={{ mt: 2 }} onClick={handleApplyPatch} disabled={!manifest}>Apply</Button>
                {applyResult && <Typography variant="body2" sx={{ mt: 1 }}>{applyResult.message}</Typography>}
              </Paper>
            </Grid>

            <Grid item xs={12} md={6}>
              <Paper sx={cardStyle(Boolean(manifest))}>
                <Typography variant="subtitle1">8. Rollback</Typography>
                <Button variant="outlined" sx={{ mt: 2 }} onClick={handleRollback} disabled={!manifest}>Rollback</Button>
                {rollbackResult && <Typography variant="body2" sx={{ mt: 1 }}>{rollbackResult.message}</Typography>}
              </Paper>
            </Grid>
          </Grid>

          <Divider sx={{ my: 3 }} />
          <Grid container spacing={2} sx={{ mb: 2 }}>
            <Grid item xs={12} md={6}>
              <Paper sx={{ p: 2, bgcolor: '#1a2233', color: 'white' }}>
                <Typography variant="subtitle2">Frontend Activity Log</Typography>
                <List dense sx={{ maxHeight: 220, overflow: 'auto' }}>
                  {appLogs.map((line, idx) => (
                    <ListItem key={`${idx}-${line}`}><ListItemText primaryTypographyProps={{ variant: 'caption' }} primary={line} /></ListItem>
                  ))}
                </List>
              </Paper>
            </Grid>
            <Grid item xs={12} md={6}>
              <Paper sx={{ p: 2, bgcolor: '#1a2233', color: 'white' }}>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <Typography variant="subtitle2">Backend Log Tail</Typography>
                  <Button size="small" variant="outlined" onClick={handleLoadBackendLogs}>Load Logs</Button>
                </Box>
                <List dense sx={{ maxHeight: 220, overflow: 'auto' }}>
                  {backendLogs.map((line, idx) => (
                    <ListItem key={`${idx}-${line}`}><ListItemText primaryTypographyProps={{ variant: 'caption' }} primary={line} /></ListItem>
                  ))}
                </List>
              </Paper>
            </Grid>
          </Grid>
        </Paper>
      </Container>
    </Box>
  );
}
