import React, { useState } from 'react';
import styled from 'styled-components';
import { FileText, Download, ZoomIn, ZoomOut, ChevronLeft, ChevronRight } from 'lucide-react';
import { documentAPI } from '../services/api';

const Container = styled.div`
  display: flex;
  flex-direction: column;
  height: 100%;
`;

const Toolbar = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem;
  border-bottom: 1px solid #e0e0e0;
  background: #f9fafb;
`;

const ToolbarLeft = styled.div`
  display: flex;
  align-items: center;
  gap: 1rem;
`;

const ToolbarRight = styled.div`
  display: flex;
  align-items: center;
  gap: 0.5rem;
`;

const Button = styled.button`
  padding: 0.5rem 1rem;
  background: ${props => props.primary ? '#667eea' : 'white'};
  color: ${props => props.primary ? 'white' : '#333'};
  border: 1px solid ${props => props.primary ? '#667eea' : '#ddd'};
  border-radius: 6px;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  cursor: pointer;
  font-size: 0.9rem;
  transition: all 0.2s;

  &:hover {
    background: ${props => props.primary ? '#5568d3' : '#f5f5f5'};
  }

  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }
`;

const IconButton = styled.button`
  padding: 0.5rem;
  background: white;
  border: 1px solid #ddd;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    background: #f5f5f5;
  }

  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }
`;

const PageInfo = styled.div`
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.9rem;
  color: #666;

  input {
    width: 50px;
    padding: 0.25rem;
    text-align: center;
    border: 1px solid #ddd;
    border-radius: 4px;
  }
`;

const ViewerContainer = styled.div`
  flex: 1;
  overflow: auto;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #e0e0e0;
  padding: 2rem;
`;

const EmptyState = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 1rem;
  color: #999;
`;

const PdfFrame = styled.iframe`
  width: 100%;
  height: 100%;
  border: none;
  background: white;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
`;

const DocumentViewer = ({ document }) => {
  const [currentPage, setCurrentPage] = useState(1);
  const [zoom, setZoom] = useState(100);
  const [pdfUrl, setPdfUrl] = useState(null);

  const handleDownload = async () => {
    if (!document) return;

    try {
      const response = await documentAPI.download(document.id);
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = window.document.createElement('a');
      link.href = url;
      link.setAttribute('download', document.originalFileName);
      window.document.body.appendChild(link);
      link.click();
      link.remove();
    } catch (error) {
      console.error('Error downloading document:', error);
    }
  };

  const handleZoomIn = () => {
    setZoom(prev => Math.min(prev + 10, 200));
  };

  const handleZoomOut = () => {
    setZoom(prev => Math.max(prev - 10, 50));
  };

  const handlePageChange = (newPage) => {
    if (document && newPage >= 1 && newPage <= document.pageCount) {
      setCurrentPage(newPage);
    }
  };

  // Load PDF when document changes
  React.useEffect(() => {
    if (document) {
      // In a real implementation, you would load the PDF here
      // For now, we'll just show a placeholder
      setPdfUrl(`/api/documents/${document.id}/download`);
    }
  }, [document]);

  if (!document) {
    return (
      <Container>
        <Toolbar>
          <ToolbarLeft>
            <span>문서 뷰어</span>
          </ToolbarLeft>
        </Toolbar>
        <ViewerContainer>
          <EmptyState>
            <FileText size={64} color="#ddd" />
            <div>문서를 선택해주세요</div>
          </EmptyState>
        </ViewerContainer>
      </Container>
    );
  }

  return (
    <Container>
      <Toolbar>
        <ToolbarLeft>
          <strong>{document.originalFileName}</strong>
        </ToolbarLeft>
        <ToolbarRight>
          <PageInfo>
            <IconButton onClick={() => handlePageChange(currentPage - 1)}>
              <ChevronLeft size={16} />
            </IconButton>
            <input
              type="number"
              value={currentPage}
              onChange={(e) => handlePageChange(parseInt(e.target.value) || 1)}
              min={1}
              max={document.pageCount}
            />
            <span>/ {document.pageCount}</span>
            <IconButton onClick={() => handlePageChange(currentPage + 1)}>
              <ChevronRight size={16} />
            </IconButton>
          </PageInfo>

          <IconButton onClick={handleZoomOut}>
            <ZoomOut size={16} />
          </IconButton>
          <span>{zoom}%</span>
          <IconButton onClick={handleZoomIn}>
            <ZoomIn size={16} />
          </IconButton>

          <Button primary onClick={handleDownload}>
            <Download size={16} />
            다운로드
          </Button>
        </ToolbarRight>
      </Toolbar>

      <ViewerContainer>
        {document.status === 'COMPLETED' ? (
          <PdfFrame
            src={pdfUrl}
            title={document.originalFileName}
            style={{ transform: `scale(${zoom / 100})` }}
          />
        ) : (
          <EmptyState>
            <div>문서 처리 중입니다...</div>
            <div>상태: {document.status}</div>
          </EmptyState>
        )}
      </ViewerContainer>
    </Container>
  );
};

export default DocumentViewer;
