import React, { useState, useEffect } from 'react';
import styled from 'styled-components';
import { Search, FileText, Calendar, User } from 'lucide-react';
import { documentAPI } from '../services/api';

const Container = styled.div`
  display: flex;
  flex-direction: column;
  height: 100%;
`;

const SearchBar = styled.div`
  padding: 1rem;
  border-bottom: 1px solid #e0e0e0;
`;

const SearchInput = styled.div`
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 8px;
  background: #f9fafb;

  &:focus-within {
    border-color: #667eea;
    background: white;
  }

  input {
    flex: 1;
    border: none;
    background: none;
    font-size: 0.9rem;

    &:focus {
      outline: none;
    }
  }
`;

const ListContainer = styled.div`
  flex: 1;
  overflow-y: auto;
  padding: 0.5rem;
`;

const DocumentItem = styled.div`
  padding: 1rem;
  margin-bottom: 0.5rem;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  background: ${props => props.selected ? '#f0f4ff' : 'white'};
  border-color: ${props => props.selected ? '#667eea' : '#e0e0e0'};

  &:hover {
    background: #f9fafb;
    border-color: #667eea;
  }
`;

const DocumentTitle = styled.div`
  font-weight: 600;
  font-size: 0.95rem;
  color: #333;
  margin-bottom: 0.5rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
`;

const DocumentMeta = styled.div`
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
  font-size: 0.8rem;
  color: #666;
`;

const MetaItem = styled.div`
  display: flex;
  align-items: center;
  gap: 0.5rem;
`;

const StatusBadge = styled.span`
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-size: 0.75rem;
  font-weight: 500;
  background: ${props => {
    switch (props.status) {
      case 'COMPLETED': return '#d4edda';
      case 'PROCESSING': return '#fff3cd';
      case 'FAILED': return '#f8d7da';
      default: return '#e2e3e5';
    }
  }};
  color: ${props => {
    switch (props.status) {
      case 'COMPLETED': return '#155724';
      case 'PROCESSING': return '#856404';
      case 'FAILED': return '#721c24';
      default: return '#383d41';
    }
  }};
`;

const EmptyState = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #999;
  gap: 1rem;
`;

const DocumentList = ({ onDocumentSelect }) => {
  const [documents, setDocuments] = useState([]);
  const [selectedId, setSelectedId] = useState(null);
  const [searchKeyword, setSearchKeyword] = useState('');
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    fetchDocuments();
  }, []);

  const fetchDocuments = async () => {
    setLoading(true);
    try {
      const response = await documentAPI.getList({
        page: 0,
        size: 50,
      });
      setDocuments(response.data.content || []);
    } catch (error) {
      console.error('Error fetching documents:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = async () => {
    if (!searchKeyword.trim()) {
      fetchDocuments();
      return;
    }

    setLoading(true);
    try {
      const response = await documentAPI.search(searchKeyword, {
        page: 0,
        size: 50,
      });
      setDocuments(response.data.content || []);
    } catch (error) {
      console.error('Error searching documents:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleDocumentClick = (document) => {
    setSelectedId(document.id);
    onDocumentSelect(document);
  };

  const getStatusLabel = (status) => {
    const labels = {
      'UPLOADING': '업로드중',
      'PROCESSING': '처리중',
      'COMPLETED': '완료',
      'FAILED': '실패',
    };
    return labels[status] || status;
  };

  return (
    <Container>
      <SearchBar>
        <SearchInput>
          <Search size={18} color="#999" />
          <input
            type="text"
            placeholder="문서 검색..."
            value={searchKeyword}
            onChange={(e) => setSearchKeyword(e.target.value)}
            onKeyPress={(e) => e.key === 'Enter' && handleSearch()}
          />
        </SearchInput>
      </SearchBar>

      <ListContainer>
        {loading ? (
          <EmptyState>
            <div>로딩중...</div>
          </EmptyState>
        ) : documents.length === 0 ? (
          <EmptyState>
            <FileText size={48} color="#ddd" />
            <div>문서가 없습니다</div>
          </EmptyState>
        ) : (
          documents.map(doc => (
            <DocumentItem
              key={doc.id}
              selected={selectedId === doc.id}
              onClick={() => handleDocumentClick(doc)}
            >
              <DocumentTitle>
                <FileText size={16} />
                {doc.originalFileName}
              </DocumentTitle>
              <DocumentMeta>
                <MetaItem>
                  <StatusBadge status={doc.status}>
                    {getStatusLabel(doc.status)}
                  </StatusBadge>
                  <span>{doc.pageCount}페이지</span>
                </MetaItem>
                <MetaItem>
                  <User size={14} />
                  {doc.uploadedBy}
                </MetaItem>
                <MetaItem>
                  <Calendar size={14} />
                  {new Date(doc.createdAt).toLocaleDateString('ko-KR')}
                </MetaItem>
              </DocumentMeta>
            </DocumentItem>
          ))
        )}
      </ListContainer>
    </Container>
  );
};

export default DocumentList;
