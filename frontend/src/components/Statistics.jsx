import React, { useState, useEffect } from 'react';
import styled from 'styled-components';
import { BarChart3, FileText, MessageSquare, Users, TrendingUp, Clock, Loader } from 'lucide-react';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer, PieChart, Pie, Cell } from 'recharts';
import { statisticsAPI } from '../services/api';

const StatisticsContainer = styled.div`
  display: flex;
  flex-direction: column;
  gap: 2rem;
  padding: 2rem;
  overflow-y: auto;
  max-height: 100%;
`;

const Title = styled.h1`
  font-size: 2rem;
  font-weight: 700;
  color: #1f2937;
  margin: 0;
`;

const Section = styled.div`
  display: flex;
  flex-direction: column;
  gap: 1rem;
`;

const SectionTitle = styled.h2`
  font-size: 1.5rem;
  font-weight: 600;
  color: #374151;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 0.5rem;
`;

const CardGrid = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1.5rem;
`;

const Card = styled.div`
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  border-left: 4px solid ${props => props.color || '#667eea'};
`;

const CardHeader = styled.div`
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-bottom: 1rem;
`;

const CardIcon = styled.div`
  width: 40px;
  height: 40px;
  border-radius: 8px;
  background: ${props => props.color || '#667eea'}22;
  display: flex;
  align-items: center;
  justify-content: center;
  color: ${props => props.color || '#667eea'};
`;

const CardTitle = styled.div`
  font-size: 0.875rem;
  font-weight: 600;
  color: #64748b;
`;

const CardValue = styled.div`
  font-size: 2rem;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 0.5rem;
`;

const CardSubtext = styled.div`
  font-size: 0.875rem;
  color: #64748b;
`;

const ChartCard = styled.div`
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
`;

const ChartTitle = styled.h3`
  font-size: 1.125rem;
  font-weight: 600;
  color: #374151;
  margin: 0 0 1.5rem 0;
`;

const Table = styled.table`
  width: 100%;
  border-collapse: collapse;
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
`;

const Th = styled.th`
  padding: 1rem;
  text-align: left;
  background: #f9fafb;
  font-weight: 600;
  color: #374151;
  border-bottom: 1px solid #e5e7eb;
`;

const Td = styled.td`
  padding: 1rem;
  border-bottom: 1px solid #f3f4f6;
  color: #64748b;
`;

const LoadingContainer = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 400px;
  gap: 1rem;
`;

const LoadingText = styled.div`
  font-size: 1rem;
  color: #64748b;
`;

const ErrorContainer = styled.div`
  background: #fef2f2;
  border: 1px solid #fecaca;
  border-radius: 12px;
  padding: 1.5rem;
  color: #991b1b;
  text-align: center;
`;

const COLORS = ['#667eea', '#f472b6', '#34d399', '#fbbf24', '#fb923c', '#a78bfa', '#60a5fa'];

const PROMPT_TYPE_LABELS = {
  BASIC: '기본',
  STRUCTURED: '구조화',
  SIMPLE: '간단',
  DETAILED: '상세',
  POINT: '핵심포인트',
  FACT_CHECK: '사실확인',
  STEP_BY_STEP: '단계별'
};

const Statistics = () => {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [statistics, setStatistics] = useState(null);

  useEffect(() => {
    fetchStatistics();
  }, []);

  const fetchStatistics = async () => {
    try {
      setLoading(true);
      const response = await statisticsAPI.getAll();
      setStatistics(response.data);
      setError(null);
    } catch (err) {
      setError(err.response?.data?.message || '통계 데이터를 불러오는데 실패했습니다.');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <StatisticsContainer>
        <LoadingContainer>
          <Loader size={48} color="#667eea" className="spinner" />
          <LoadingText>통계 데이터를 불러오는 중...</LoadingText>
        </LoadingContainer>
      </StatisticsContainer>
    );
  }

  if (error) {
    return (
      <StatisticsContainer>
        <Title>통계 조회</Title>
        <ErrorContainer>{error}</ErrorContainer>
      </StatisticsContainer>
    );
  }

  const docStats = statistics?.documentStatistics || {};
  const queryStats = statistics?.queryStatistics || {};

  // 프롬프트 타입별 차트 데이터
  const promptTypeData = Object.entries(queryStats.promptTypeCounts || {}).map(([key, value]) => ({
    name: PROMPT_TYPE_LABELS[key] || key,
    count: value,
    avgTime: queryStats.averageResponseTimes?.[key] || 0
  }));

  // 문서 상태별 차트 데이터
  const statusData = Object.entries(docStats.statusCounts || {}).map(([key, value]) => ({
    name: key,
    value: value
  }));

  return (
    <StatisticsContainer>
      <Title>통계 조회</Title>

      {/* 메타DB 현황 */}
      <Section>
        <SectionTitle>
          <FileText size={24} />
          메타DB 현황
        </SectionTitle>

        <CardGrid>
          <Card color="#667eea">
            <CardHeader>
              <CardIcon color="#667eea">
                <FileText size={20} />
              </CardIcon>
              <CardTitle>총 문서 수</CardTitle>
            </CardHeader>
            <CardValue>{docStats.totalDocuments || 0}</CardValue>
            <CardSubtext>전체 등록된 문서</CardSubtext>
          </Card>

          <Card color="#34d399">
            <CardHeader>
              <CardIcon color="#34d399">
                <BarChart3 size={20} />
              </CardIcon>
              <CardTitle>완료된 문서</CardTitle>
            </CardHeader>
            <CardValue>{docStats.completedDocuments || 0}</CardValue>
            <CardSubtext>처리 완료</CardSubtext>
          </Card>

          <Card color="#fbbf24">
            <CardHeader>
              <CardIcon color="#fbbf24">
                <Clock size={20} />
              </CardIcon>
              <CardTitle>처리 중인 문서</CardTitle>
            </CardHeader>
            <CardValue>{docStats.processingDocuments || 0}</CardValue>
            <CardSubtext>벡터DB 등록 중</CardSubtext>
          </Card>

          <Card color="#ef4444">
            <CardHeader>
              <CardIcon color="#ef4444">
                <TrendingUp size={20} />
              </CardIcon>
              <CardTitle>실패한 문서</CardTitle>
            </CardHeader>
            <CardValue>{docStats.failedDocuments || 0}</CardValue>
            <CardSubtext>처리 실패</CardSubtext>
          </Card>

          <Card color="#a78bfa">
            <CardHeader>
              <CardIcon color="#a78bfa">
                <FileText size={20} />
              </CardIcon>
              <CardTitle>총 페이지 수</CardTitle>
            </CardHeader>
            <CardValue>{docStats.totalPages || 0}</CardValue>
            <CardSubtext>모든 문서 합계</CardSubtext>
          </Card>

          <Card color="#60a5fa">
            <CardHeader>
              <CardIcon color="#60a5fa">
                <BarChart3 size={20} />
              </CardIcon>
              <CardTitle>총 저장 용량</CardTitle>
            </CardHeader>
            <CardValue>{((docStats.totalSize || 0) / (1024 * 1024)).toFixed(2)}</CardValue>
            <CardSubtext>MB</CardSubtext>
          </Card>
        </CardGrid>

        {statusData.length > 0 && (
          <ChartCard>
            <ChartTitle>문서 상태별 분포</ChartTitle>
            <ResponsiveContainer width="100%" height={300}>
              <PieChart>
                <Pie
                  data={statusData}
                  cx="50%"
                  cy="50%"
                  labelLine={false}
                  label={({ name, percent }) => `${name} ${(percent * 100).toFixed(0)}%`}
                  outerRadius={100}
                  fill="#8884d8"
                  dataKey="value"
                >
                  {statusData.map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                  ))}
                </Pie>
                <Tooltip />
                <Legend />
              </PieChart>
            </ResponsiveContainer>
          </ChartCard>
        )}
      </Section>

      {/* 프롬프트 방식별 이용 현황 */}
      <Section>
        <SectionTitle>
          <MessageSquare size={24} />
          프롬프트 방식별 이용 현황
        </SectionTitle>

        <CardGrid>
          <Card color="#667eea">
            <CardHeader>
              <CardIcon color="#667eea">
                <MessageSquare size={20} />
              </CardIcon>
              <CardTitle>총 질의 수</CardTitle>
            </CardHeader>
            <CardValue>{queryStats.totalQueries || 0}</CardValue>
            <CardSubtext>전체 질의 건수</CardSubtext>
          </Card>

          <Card color="#f472b6">
            <CardHeader>
              <CardIcon color="#f472b6">
                <TrendingUp size={20} />
              </CardIcon>
              <CardTitle>오늘 질의</CardTitle>
            </CardHeader>
            <CardValue>{queryStats.queriesToday || 0}</CardValue>
            <CardSubtext>금일 질의 건수</CardSubtext>
          </Card>

          <Card color="#34d399">
            <CardHeader>
              <CardIcon color="#34d399">
                <BarChart3 size={20} />
              </CardIcon>
              <CardTitle>이번 주 질의</CardTitle>
            </CardHeader>
            <CardValue>{queryStats.queriesThisWeek || 0}</CardValue>
            <CardSubtext>주간 질의 건수</CardSubtext>
          </Card>

          <Card color="#fbbf24">
            <CardHeader>
              <CardIcon color="#fbbf24">
                <Clock size={20} />
              </CardIcon>
              <CardTitle>이번 달 질의</CardTitle>
            </CardHeader>
            <CardValue>{queryStats.queriesThisMonth || 0}</CardValue>
            <CardSubtext>월간 질의 건수</CardSubtext>
          </Card>
        </CardGrid>

        {promptTypeData.length > 0 && (
          <>
            <ChartCard>
              <ChartTitle>프롬프트 타입별 사용 횟수</ChartTitle>
              <ResponsiveContainer width="100%" height={300}>
                <BarChart data={promptTypeData}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="name" />
                  <YAxis />
                  <Tooltip />
                  <Legend />
                  <Bar dataKey="count" fill="#667eea" name="사용 횟수" />
                </BarChart>
              </ResponsiveContainer>
            </ChartCard>

            <ChartCard>
              <ChartTitle>프롬프트 타입별 평균 응답 시간 (ms)</ChartTitle>
              <ResponsiveContainer width="100%" height={300}>
                <BarChart data={promptTypeData}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="name" />
                  <YAxis />
                  <Tooltip />
                  <Legend />
                  <Bar dataKey="avgTime" fill="#34d399" name="평균 응답 시간" />
                </BarChart>
              </ResponsiveContainer>
            </ChartCard>
          </>
        )}
      </Section>

      {/* 상위 사용자 */}
      {queryStats.topUsers && queryStats.topUsers.length > 0 && (
        <Section>
          <SectionTitle>
            <Users size={24} />
            상위 사용자 (Top 10)
          </SectionTitle>

          <Table>
            <thead>
              <tr>
                <Th>순위</Th>
                <Th>사용자명</Th>
                <Th>부서</Th>
                <Th>질의 횟수</Th>
              </tr>
            </thead>
            <tbody>
              {queryStats.topUsers.map((user, index) => (
                <tr key={index}>
                  <Td>{index + 1}</Td>
                  <Td>{user.userName}</Td>
                  <Td>{user.department || '-'}</Td>
                  <Td>{user.queryCount}</Td>
                </tr>
              ))}
            </tbody>
          </Table>
        </Section>
      )}
    </StatisticsContainer>
  );
};

export default Statistics;
