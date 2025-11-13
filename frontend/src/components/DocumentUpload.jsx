import React, { useState } from 'react';
import styled from 'styled-components';
import { Upload, FileText, AlertCircle, CheckCircle, Loader } from 'lucide-react';
import { documentAPI } from '../services/api';

const UploadContainer = styled.div`
  display: flex;
  flex-direction: column;
  gap: 2rem;
  padding: 2rem;
  max-width: 800px;
  margin: 0 auto;
`;

const Title = styled.h1`
  font-size: 2rem;
  font-weight: 700;
  color: #1f2937;
  margin: 0;
`;

const Card = styled.div`
  background: white;
  border-radius: 12px;
  padding: 2rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
`;

const DropZone = styled.div`
  border: 2px dashed ${props => props.isDragging ? '#667eea' : '#cbd5e1'};
  border-radius: 12px;
  padding: 3rem;
  text-align: center;
  background: ${props => props.isDragging ? '#f0f4ff' : '#f9fafb'};
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    border-color: #667eea;
    background: #f0f4ff;
  }
`;

const UploadIcon = styled(Upload)`
  margin: 0 auto 1rem;
  color: #667eea;
`;

const DropZoneText = styled.p`
  font-size: 1rem;
  color: #64748b;
  margin: 0.5rem 0;
`;

const DropZoneHint = styled.p`
  font-size: 0.875rem;
  color: #94a3b8;
  margin: 0;
`;

const FileInput = styled.input`
  display: none;
`;

const FormGroup = styled.div`
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
`;

const Label = styled.label`
  font-size: 0.875rem;
  font-weight: 600;
  color: #374151;
`;

const Input = styled.input`
  padding: 0.75rem;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  font-size: 1rem;
  transition: border-color 0.2s;

  &:focus {
    outline: none;
    border-color: #667eea;
  }
`;

const TextArea = styled.textarea`
  padding: 0.75rem;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  font-size: 1rem;
  min-height: 100px;
  resize: vertical;
  transition: border-color 0.2s;

  &:focus {
    outline: none;
    border-color: #667eea;
  }
`;

const SelectedFile = styled.div`
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem;
  background: #f0f4ff;
  border-radius: 8px;
  margin-top: 1rem;
`;

const FileInfo = styled.div`
  flex: 1;
`;

const FileName = styled.div`
  font-weight: 600;
  color: #1f2937;
`;

const FileSize = styled.div`
  font-size: 0.875rem;
  color: #64748b;
`;

const RemoveButton = styled.button`
  padding: 0.5rem 1rem;
  background: #ef4444;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.875rem;

  &:hover {
    background: #dc2626;
  }
`;

const ButtonGroup = styled.div`
  display: flex;
  gap: 1rem;
  justify-content: flex-end;
`;

const Button = styled.button`
  padding: 0.75rem 2rem;
  background: ${props => props.variant === 'secondary' ? '#f3f4f6' : '#667eea'};
  color: ${props => props.variant === 'secondary' ? '#374151' : 'white'};
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 0.5rem;

  &:hover {
    background: ${props => props.variant === 'secondary' ? '#e5e7eb' : '#5568d3'};
  }

  &:disabled {
    background: #cbd5e1;
    cursor: not-allowed;
  }
`;

const Alert = styled.div`
  display: flex;
  align-items: flex-start;
  gap: 0.75rem;
  padding: 1rem;
  background: ${props => {
    switch(props.type) {
      case 'error': return '#fef2f2';
      case 'success': return '#f0fdf4';
      default: return '#f0f4ff';
    }
  }};
  border: 1px solid ${props => {
    switch(props.type) {
      case 'error': return '#fecaca';
      case 'success': return '#bbf7d0';
      default: return '#c7d2fe';
    }
  }};
  border-radius: 8px;
  color: ${props => {
    switch(props.type) {
      case 'error': return '#991b1b';
      case 'success': return '#166534';
      default: return '#3730a3';
    }
  }};
`;

const ProgressBar = styled.div`
  width: 100%;
  height: 8px;
  background: #e5e7eb;
  border-radius: 4px;
  overflow: hidden;
  margin-top: 1rem;
`;

const ProgressFill = styled.div`
  height: 100%;
  background: #667eea;
  width: ${props => props.progress}%;
  transition: width 0.3s;
`;

const DocumentUpload = () => {
  const [isDragging, setIsDragging] = useState(false);
  const [selectedFile, setSelectedFile] = useState(null);
  const [uploadedBy, setUploadedBy] = useState('홍길동');
  const [description, setDescription] = useState('');
  const [uploading, setUploading] = useState(false);
  const [uploadProgress, setUploadProgress] = useState(0);
  const [alert, setAlert] = useState(null);

  const formatFileSize = (bytes) => {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i];
  };

  const handleDragOver = (e) => {
    e.preventDefault();
    setIsDragging(true);
  };

  const handleDragLeave = (e) => {
    e.preventDefault();
    setIsDragging(false);
  };

  const handleDrop = (e) => {
    e.preventDefault();
    setIsDragging(false);

    const files = e.dataTransfer.files;
    if (files.length > 0) {
      handleFileSelect(files[0]);
    }
  };

  const handleFileSelect = (file) => {
    if (file.type !== 'application/pdf') {
      setAlert({ type: 'error', message: 'PDF 파일만 업로드 가능합니다.' });
      return;
    }

    if (file.size > 100 * 1024 * 1024) { // 100MB
      setAlert({ type: 'error', message: '파일 크기는 100MB를 초과할 수 없습니다.' });
      return;
    }

    setSelectedFile(file);
    setAlert(null);
  };

  const handleFileInputChange = (e) => {
    const files = e.target.files;
    if (files.length > 0) {
      handleFileSelect(files[0]);
    }
  };

  const handleRemoveFile = () => {
    setSelectedFile(null);
  };

  const handleReset = () => {
    setSelectedFile(null);
    setDescription('');
    setAlert(null);
    setUploadProgress(0);
  };

  const handleSubmit = async () => {
    if (!selectedFile) {
      setAlert({ type: 'error', message: '파일을 선택해주세요.' });
      return;
    }

    if (!uploadedBy.trim()) {
      setAlert({ type: 'error', message: '업로드자 이름을 입력해주세요.' });
      return;
    }

    setUploading(true);
    setUploadProgress(0);
    setAlert(null);

    try {
      const formData = new FormData();
      formData.append('file', selectedFile);
      formData.append('uploadedBy', uploadedBy);
      formData.append('description', description);

      // 업로드 진행률 시뮬레이션 (실제로는 axios onUploadProgress 사용 가능)
      const progressInterval = setInterval(() => {
        setUploadProgress(prev => {
          if (prev >= 90) {
            clearInterval(progressInterval);
            return 90;
          }
          return prev + 10;
        });
      }, 200);

      const response = await documentAPI.upload(formData);

      clearInterval(progressInterval);
      setUploadProgress(100);

      setAlert({
        type: 'success',
        message: '문서가 성공적으로 업로드되었습니다. 벡터DB 및 메타데이터 등록이 진행됩니다.'
      });

      // 3초 후 폼 초기화
      setTimeout(() => {
        handleReset();
        setUploading(false);
      }, 3000);

    } catch (error) {
      setUploading(false);
      setUploadProgress(0);

      const errorMessage = error.response?.data?.message ||
                          error.message ||
                          '문서 업로드 중 오류가 발생했습니다.';

      setAlert({ type: 'error', message: errorMessage });
    }
  };

  return (
    <UploadContainer>
      <Title>문서 등록</Title>

      <Card>
        <DropZone
          isDragging={isDragging}
          onDragOver={handleDragOver}
          onDragLeave={handleDragLeave}
          onDrop={handleDrop}
          onClick={() => document.getElementById('file-input').click()}
        >
          <UploadIcon size={48} />
          <DropZoneText>
            클릭하거나 파일을 드래그하여 업로드
          </DropZoneText>
          <DropZoneHint>
            PDF 파일만 가능 (최대 100MB)
          </DropZoneHint>
        </DropZone>

        <FileInput
          id="file-input"
          type="file"
          accept=".pdf"
          onChange={handleFileInputChange}
        />

        {selectedFile && (
          <SelectedFile>
            <FileText size={24} color="#667eea" />
            <FileInfo>
              <FileName>{selectedFile.name}</FileName>
              <FileSize>{formatFileSize(selectedFile.size)}</FileSize>
            </FileInfo>
            <RemoveButton onClick={handleRemoveFile}>제거</RemoveButton>
          </SelectedFile>
        )}
      </Card>

      <Card>
        <FormGroup>
          <Label>업로드자</Label>
          <Input
            type="text"
            value={uploadedBy}
            onChange={(e) => setUploadedBy(e.target.value)}
            placeholder="이름을 입력하세요"
          />
        </FormGroup>

        <FormGroup style={{ marginTop: '1.5rem' }}>
          <Label>문서 설명</Label>
          <TextArea
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            placeholder="문서에 대한 설명을 입력하세요 (선택사항)"
          />
        </FormGroup>
      </Card>

      {alert && (
        <Alert type={alert.type}>
          {alert.type === 'error' && <AlertCircle size={20} />}
          {alert.type === 'success' && <CheckCircle size={20} />}
          <div>{alert.message}</div>
        </Alert>
      )}

      {uploading && (
        <ProgressBar>
          <ProgressFill progress={uploadProgress} />
        </ProgressBar>
      )}

      <ButtonGroup>
        <Button
          variant="secondary"
          onClick={handleReset}
          disabled={uploading}
        >
          초기화
        </Button>
        <Button
          onClick={handleSubmit}
          disabled={uploading || !selectedFile}
        >
          {uploading ? (
            <>
              <Loader size={20} className="spinner" />
              업로드 중...
            </>
          ) : (
            <>
              <Upload size={20} />
              업로드
            </>
          )}
        </Button>
      </ButtonGroup>
    </UploadContainer>
  );
};

export default DocumentUpload;
