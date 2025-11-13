# 의약품 허가심사 검토 프로그램

의약품 허가심사 문서를 효율적으로 관리하고 AI 기반 질의응답을 제공하는 통합 시스템입니다.

## 주요 기능

### 1. 문서 업로드 기능
- PDF 파일 업로드
- 자동 텍스트 추출 및 벡터DB 저장
- 메타데이터 RDB 저장
- 문서 처리 상태 추적

### 2. 문서 보기
- 업로드된 파일 목록 및 검색
- PDF 뷰어로 문서 내용 확인
- 검색 결과 하이라이트

### 3. 질문 및 답변 (AI 챗봇)
- 7가지 프롬프트 방식 선택
  - 기본
  - 구조화
  - 간단
  - 상세
  - 포인트
  - 사실 확인
  - 단계별 사고
- 문서 기반 컨텍스트 검색 (RAG)
- 실시간 AI 응답 생성

### 4. 질문 이력 관리
- 모든 질문/답변 이력 저장
- 사용자별 조회 가능
- 상세 검색 및 필터링

### 5. 통계 기능
- 문서 관리 현황
- 프롬프트 방식별 이용 통계
- 사용자별 활동 분석

## 기술 스택

### Frontend
- **React 18** - UI 프레임워크
- **Vite** - 빌드 도구
- **Styled Components** - CSS-in-JS
- **Axios** - HTTP 클라이언트
- **React Router** - 라우팅
- **Lucide React** - 아이콘

### Backend
- **Spring Boot 3.2** - 애플리케이션 프레임워크
- **Java 17** - 프로그래밍 언어
- **Spring Data JPA** - ORM
- **Spring WebFlux** - 비동기 HTTP 클라이언트
- **Apache PDFBox** - PDF 처리

### 데이터베이스 및 검색
- **MySQL 8.0** - 관계형 데이터베이스
- **ChromaDB** - 벡터 데이터베이스
- **OpenSearch 2.11** - 전문 검색 엔진

### AI/ML
- **Ollama** - 로컬 LLM 실행 환경
- **Llama 3.2** - 대규모 언어 모델 (기본)

### 인프라
- **Docker** - 컨테이너화
- **Docker Compose** - 멀티 컨테이너 오케스트레이션

## 시스템 요구사항

### 개발 환경
- **Java 17** 이상
- **Node.js 18** 이상
- **Maven 3.8** 이상 (또는 Maven Wrapper 사용)
- **Docker 20.10** 이상
- **Docker Compose 2.0** 이상

### 시스템 리소스
- 최소 16GB RAM (Ollama LLM 실행용)
- 최소 50GB 디스크 공간

### 권장 IDE
- **VSCode** with Java Extension Pack
- **IntelliJ IDEA** (또는 다른 Java IDE)

## 설치 및 실행

> **💡 VSCode 사용자**: [VSCode 실행 가이드](VSCODE_GUIDE.md)를 참조하세요.

### 빠른 시작 (스크립트 사용)

```bash
# 1. 전체 인프라 시작
./start-all.sh

# 2. Backend 실행 (새 터미널)
./start-backend.sh

# 3. Frontend 실행 (새 터미널)
./start-frontend.sh
```

브라우저에서 http://localhost:3000 접속

### VSCode에서 실행

1. **프로젝트 열기**
   ```bash
   code ai_drug_approval_test
   ```

2. **권장 확장 프로그램 설치** (자동 프롬프트 표시)
   - Java Extension Pack
   - Spring Boot Extension Pack
   - ESLint, Prettier

3. **인프라 시작**
   - `Ctrl+Shift+P` → "Tasks: Run Task" → "Docker: Start Infrastructure"

4. **애플리케이션 실행**
   - `F5` 키 → "Full Stack" 선택
   - Backend와 Frontend가 자동으로 실행됩니다

상세한 내용은 [VSCODE_GUIDE.md](VSCODE_GUIDE.md)를 참조하세요.

## 상세 설치 및 실행

### 1. 저장소 클론

```bash
git clone <repository-url>
cd ai_drug_approval_test
```

### 2. Docker Compose로 전체 시스템 실행

```bash
# 인프라 서비스 시작 (MySQL, ChromaDB, OpenSearch, Ollama)
docker-compose up -d mysql chromadb opensearch ollama

# Ollama 모델 다운로드 (처음 실행 시)
docker exec -it drug-approval-ollama ollama pull llama3.2

# 모든 서비스 확인
docker-compose ps
```

### 3. 백엔드 실행

#### 방법 1: Docker로 실행 (권장)

```bash
# docker-compose.yml에서 backend 섹션 주석 해제 후
docker-compose up -d backend
```

#### 방법 2: 로컬에서 실행

```bash
cd backend
./mvnw clean install
./mvnw spring-boot:run
```

백엔드는 http://localhost:8080에서 실행됩니다.

### 4. 프론트엔드 실행

#### 방법 1: Docker로 실행 (권장)

```bash
# docker-compose.yml에서 frontend 섹션 주석 해제 후
docker-compose up -d frontend
```

#### 방법 2: 로컬에서 실행

```bash
cd frontend
npm install
npm run dev
```

프론트엔드는 http://localhost:3000에서 실행됩니다.

## 화면 구성

### 상단 헤더
- 시스템명
- 사용자 정보 (이름, 부서, 접속시간)
- 알림 아이콘

### 좌측 패널 - AI 챗봇
- 프롬프트 방식 선택
- 질문 입력
- 실시간 답변 표시
- 대화 이력

### 중앙 영역
- **좌측**: 문서 검색 및 목록
- **우측**: PDF 문서 뷰어

### 우측 사이드바 (접기 가능)
- 문서 등록
- 문서 보기
- 통계 조회
- 제출 서류

## API 엔드포인트

### 문서 관리
- `POST /api/documents` - 문서 업로드
- `GET /api/documents` - 문서 목록 조회
- `GET /api/documents/search` - 문서 검색
- `GET /api/documents/{id}` - 문서 상세 조회
- `GET /api/documents/{id}/download` - 문서 다운로드
- `DELETE /api/documents/{id}` - 문서 삭제

### 질의응답
- `POST /api/queries` - 질문 처리
- `GET /api/queries/history` - 질문 이력 조회
- `GET /api/queries/history/user/{userName}` - 사용자별 이력
- `GET /api/queries/history/{id}` - 이력 상세 조회

### 통계
- `GET /api/statistics` - 전체 통계
- `GET /api/statistics/documents` - 문서 통계
- `GET /api/statistics/queries` - 질의 통계

## 환경 변수

### Backend (application.yml)

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/drug_approval
    username: drugadmin
    password: drugpassword

app:
  chroma:
    url: http://localhost:8000
    auth-token: test-token
  opensearch:
    url: http://localhost:9200
  ollama:
    url: http://localhost:11434
    model: llama3.2:latest
  upload:
    directory: ./uploads
```

### Frontend (.env)

```env
VITE_API_URL=http://localhost:8080/api
```

## 개발 가이드

### Backend 개발

```bash
cd backend

# 컴파일
./mvnw compile

# 테스트
./mvnw test

# 패키징
./mvnw package
```

### Frontend 개발

```bash
cd frontend

# 개발 서버 실행
npm run dev

# 빌드
npm run build

# 프리뷰
npm run preview
```

## 트러블슈팅

### Ollama 모델 다운로드 실패
```bash
# 컨테이너 내부에서 직접 다운로드
docker exec -it drug-approval-ollama ollama pull llama3.2
```

### ChromaDB 연결 실패
```bash
# ChromaDB 헬스 체크
curl http://localhost:8000/api/v1/heartbeat

# 로그 확인
docker logs drug-approval-chroma
```

### OpenSearch 메모리 부족
```bash
# docker-compose.yml에서 메모리 설정 조정
environment:
  - OPENSEARCH_JAVA_OPTS=-Xms1g -Xmx1g
```

### MySQL 연결 실패
```bash
# MySQL 접속 확인
docker exec -it drug-approval-mysql mysql -u drugadmin -p

# 데이터베이스 확인
SHOW DATABASES;
USE drug_approval;
SHOW TABLES;
```

## 데이터베이스 스키마

### documents 테이블
- 문서 메타데이터 저장
- 파일 정보, 페이지 수, 업로드 정보
- ChromaDB 및 OpenSearch ID 참조

### query_history 테이블
- 질문/답변 이력
- 프롬프트 타입, 응답 시간
- 관련 문서 정보 (JSON)

## 보안 고려사항

현재 버전은 개발/테스트 목적이며, 프로덕션 환경에서는 다음 사항을 반드시 고려해야 합니다:

1. Spring Security 인증/인가 구현
2. API 키 및 토큰 관리
3. HTTPS 적용
4. 파일 업로드 검증 강화
5. SQL Injection 방지
6. XSS 방지
7. CORS 정책 강화

## 라이선스

이 프로젝트는 라이선스 파일(LICENSE.md)에 따라 배포됩니다.

## 기여

버그 리포트 및 기능 제안은 Issues를 통해 제출해주세요.

## 지원

문의사항이 있으시면 프로젝트 관리자에게 연락해주세요.
