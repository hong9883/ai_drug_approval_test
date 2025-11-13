import React, { useState, useEffect } from 'react';
import styled from 'styled-components';
import DocumentList from './DocumentList';
import DocumentViewer from './DocumentViewer';

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

const MainContent = () => {
  const [selectedDocument, setSelectedDocument] = useState(null);

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
};

export default MainContent;
