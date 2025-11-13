import React, { useState } from 'react';
import styled from 'styled-components';
import DocumentList from './DocumentList';
import DocumentViewer from './DocumentViewer';
import DocumentUpload from './DocumentUpload';
import Statistics from './Statistics';

const MainContainer = styled.div`
  flex: 1;
  display: flex;
  gap: 1rem;
  padding: 1rem;
  overflow: hidden;
  background: #f9fafb;
`;

const LeftPanel = styled.div`
  width: 400px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  display: flex;
  flex-direction: column;
  overflow: hidden;
`;

const RightPanel = styled.div`
  flex: 1;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  overflow: hidden;
`;

const FullPanel = styled.div`
  flex: 1;
  background: #f9fafb;
  overflow-y: auto;
`;

const ComingSoon = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #64748b;
  font-size: 1.5rem;
  font-weight: 600;
`;

const MainContent = ({ activeMenu, currentUser }) => {
  const [selectedDocument, setSelectedDocument] = useState(null);

  // 문서보기 화면
  if (activeMenu === 'documents') {
    return (
      <MainContainer>
        <LeftPanel>
          <DocumentList onDocumentSelect={setSelectedDocument} />
        </LeftPanel>
        <RightPanel>
          <DocumentViewer document={selectedDocument} />
        </RightPanel>
      </MainContainer>
    );
  }

  // 문서등록 화면
  if (activeMenu === 'upload') {
    return (
      <MainContainer>
        <FullPanel>
          <DocumentUpload currentUser={currentUser} />
        </FullPanel>
      </MainContainer>
    );
  }

  // 통계조회 화면
  if (activeMenu === 'statistics') {
    return (
      <MainContainer>
        <FullPanel>
          <Statistics />
        </FullPanel>
      </MainContainer>
    );
  }

  // 제출서류 화면 (미구현)
  if (activeMenu === 'submissions') {
    return (
      <MainContainer>
        <FullPanel>
          <ComingSoon>
            제출서류 기능은 준비 중입니다.
          </ComingSoon>
        </FullPanel>
      </MainContainer>
    );
  }

  return null;
};

export default MainContent;
