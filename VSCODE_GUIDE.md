# VSCode에서 실행하기

이 가이드는 VSCode에서 의약품 허가심사 검토 프로그램을 실행하는 방법을 설명합니다.

## 사전 요구사항

### 필수 소프트웨어

1. **Java 17 이상**
   ```bash
   # 버전 확인
   java -version
   ```

2. **Node.js 18 이상**
   ```bash
   # 버전 확인
   node -v
   npm -v
   ```

3. **Docker & Docker Compose**
   ```bash
   # 버전 확인
   docker --version
   docker-compose --version
   ```

4. **Maven** (선택사항 - Maven Wrapper 사용 가능)
   ```bash
   # 버전 확인
   mvn -v
   ```

### VSCode 확장 프로그램

프로젝트를 열면 권장 확장 프로그램 설치 알림이 표시됩니다. 다음 확장 프로그램을 설치하세요:

#### 필수 확장 프로그램

1. **Java Extension Pack** (vscjava.vscode-java-pack)
   - Language Support for Java
   - Debugger for Java
   - Maven for Java
   - Project Manager for Java
   - Test Runner for Java

2. **Spring Boot Extension Pack** (vmware.vscode-spring-boot)
   - Spring Boot Tools
   - Spring Boot Dashboard

3. **Lombok Annotations Support** (gabrielbb.vscode-lombok)

4. **ESLint** (dbaeumer.vscode-eslint)

5. **Prettier** (esbenp.prettier-vscode)

#### 권장 확장 프로그램

- **ES7+ React/Redux/React-Native snippets** (dsznajder.es7-react-js-snippets)
- **vscode-styled-components** (styled-components.vscode-styled-components)
- **Docker** (ms-azuretools.vscode-docker)
- **YAML** (redhat.vscode-yaml)

## 빠른 시작

### 방법 1: 자동 스크립트 사용 (추천)

```bash
# 1. 전체 인프라 시작
./start-all.sh

# 2. 새 터미널에서 Backend 실행
./start-backend.sh

# 3. 새 터미널에서 Frontend 실행
./start-frontend.sh
```

### 방법 2: VSCode 실행 구성 사용

1. **인프라 서비스 시작**
   - `Ctrl+Shift+P` (또는 `Cmd+Shift+P` on Mac)
   - "Tasks: Run Task" 선택
   - "Docker: Start Infrastructure" 선택

2. **애플리케이션 실행**
   - `F5` 키 누르기
   - "Full Stack" 실행 구성 선택
   - Backend와 Frontend가 동시에 실행됩니다

3. **개별 실행**
   - Backend만: "Spring Boot - DrugApprovalApplication" 선택
   - Frontend만: "Frontend - npm dev" 선택

## 단계별 상세 가이드

### 1단계: 프로젝트 열기

```bash
# VSCode에서 프로젝트 폴더 열기
code ai_drug_approval_test
```

또는 VSCode에서 `File > Open Folder` 선택

### 2단계: 인프라 서비스 시작

#### 방법 A: VSCode Task 사용

1. `Ctrl+Shift+P` → "Tasks: Run Task"
2. "Docker: Start Infrastructure" 선택
3. 서비스가 시작될 때까지 대기 (약 30초)

#### 방법 B: 터미널 사용

```bash
# VSCode 내장 터미널 (Ctrl+`)
docker-compose up -d mysql chromadb opensearch ollama

# 서비스 상태 확인
docker-compose ps
```

### 3단계: Ollama 모델 다운로드 (처음 1회)

```bash
# VSCode Task 사용
Ctrl+Shift+P → "Tasks: Run Task" → "Ollama: Pull Model"

# 또는 터미널 사용
docker exec drug-approval-ollama ollama pull llama3.2
```

### 4단계: Backend 실행

#### 방법 A: VSCode 디버그 모드

1. 왼쪽 사이드바에서 "Run and Debug" (Ctrl+Shift+D)
2. "Spring Boot - DrugApprovalApplication" 선택
3. `F5` 또는 녹색 실행 버튼 클릭

#### 방법 B: Spring Boot Dashboard 사용

1. 왼쪽 사이드바에서 "Spring Boot Dashboard" 아이콘 클릭
2. "drug-approval-backend" 앱 찾기
3. 실행 버튼 클릭

#### 방법 C: 터미널 사용

```bash
cd backend
./mvnw spring-boot:run
```

#### Backend 시작 확인

- 콘솔에 "Started DrugApprovalApplication" 메시지 표시
- http://localhost:8080 접속 가능

### 5단계: Frontend 실행

#### 방법 A: VSCode 실행 구성

1. "Run and Debug" (Ctrl+Shift+D)
2. "Frontend - npm dev" 선택
3. `F5` 또는 실행 버튼 클릭

#### 방법 B: VSCode Task 사용

```
Ctrl+Shift+P → "Tasks: Run Task" → "npm: Dev"
```

#### 방법 C: 터미널 사용

```bash
cd frontend

# 처음 1회: 의존성 설치
npm install

# 개발 서버 실행
npm run dev
```

#### Frontend 시작 확인

- 브라우저가 자동으로 열림 (http://localhost:3000)
- 또는 수동으로 http://localhost:3000 접속

## VSCode 단축키

### 실행 및 디버깅

- `F5`: 선택한 구성으로 디버깅 시작
- `Ctrl+F5`: 디버깅 없이 실행
- `Shift+F5`: 디버깅 중지
- `Ctrl+Shift+F5`: 디버깅 재시작

### 작업 관리

- `Ctrl+Shift+P`: 명령 팔레트
- `Ctrl+Shift+B`: 빌드 작업 실행
- `Ctrl+Shift+T`: Task 실행

### 터미널

- `Ctrl+\``: 터미널 토글
- `Ctrl+Shift+\``: 새 터미널 생성

### 탐색

- `Ctrl+P`: 파일 빠른 열기
- `Ctrl+Shift+F`: 전체 프로젝트 검색
- `F12`: 정의로 이동
- `Alt+F12`: 정의 미리보기

## 디버깅

### Backend 디버깅

1. 중단점 설정: 코드 라인 번호 왼쪽 클릭
2. `F5`로 디버그 모드 실행
3. 중단점에서 멈추면:
   - `F10`: 다음 줄로 이동 (Step Over)
   - `F11`: 함수 내부로 들어가기 (Step Into)
   - `Shift+F11`: 함수에서 나오기 (Step Out)
   - `F5`: 계속 실행

### Frontend 디버깅

1. Chrome 브라우저에서 개발자 도구 열기 (F12)
2. VSCode의 디버거와 연동하려면:
   - Chrome 확장 프로그램 "Debugger for Chrome" 설치
   - `.vscode/launch.json`에 Chrome 구성 추가

## 자주 사용하는 Task

VSCode에서 `Ctrl+Shift+P` → "Tasks: Run Task"로 실행:

### Backend Tasks

- **Maven: Clean**: 빌드 파일 정리
- **Maven: Compile**: 컴파일만 수행
- **Maven: Package**: JAR 파일 생성
- **Maven: Test**: 테스트 실행

### Frontend Tasks

- **npm: Install**: 의존성 설치
- **npm: Dev**: 개발 서버 실행
- **npm: Build**: 프로덕션 빌드

### Docker Tasks

- **Docker: Start Infrastructure**: 모든 인프라 서비스 시작
- **Docker: Stop All**: 모든 Docker 서비스 중지
- **Ollama: Pull Model**: Ollama 모델 다운로드

## 환경 변수 설정

### Backend (.env)

`backend/.env` 파일에서 설정:

```env
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/drug_approval
SPRING_DATASOURCE_USERNAME=drugadmin
SPRING_DATASOURCE_PASSWORD=drugpassword
CHROMA_URL=http://localhost:8000
OPENSEARCH_URL=http://localhost:9200
OLLAMA_URL=http://localhost:11434
```

### Frontend (.env)

`frontend/.env` 파일에서 설정:

```env
VITE_API_URL=http://localhost:8080/api
```

## 트러블슈팅

### Java 확장이 작동하지 않음

1. Java Extension Pack 설치 확인
2. `Ctrl+Shift+P` → "Java: Clean Java Language Server Workspace"
3. VSCode 재시작

### Maven 빌드 실패

```bash
# Maven Wrapper 재다운로드
cd backend
rm -rf .mvn
./mvnw --version
```

### 포트 충돌

다른 프로그램이 포트를 사용 중인 경우:

```bash
# 포트 사용 확인 (Linux/Mac)
lsof -i :8080  # Backend
lsof -i :3000  # Frontend

# 프로세스 종료
kill -9 <PID>
```

### Docker 서비스 오류

```bash
# 로그 확인
docker logs drug-approval-mysql
docker logs drug-approval-chroma
docker logs drug-approval-opensearch
docker logs drug-approval-ollama

# 서비스 재시작
docker-compose restart <service-name>

# 전체 재시작
docker-compose down
docker-compose up -d
```

### Frontend 의존성 오류

```bash
cd frontend
rm -rf node_modules package-lock.json
npm install
```

## 개발 워크플로우

### 1. 기능 개발

```bash
# 1. 새 브랜치 생성
git checkout -b feature/new-feature

# 2. 코드 작성

# 3. Backend 테스트
cd backend
./mvnw test

# 4. Frontend 빌드 테스트
cd frontend
npm run build

# 5. 커밋
git add .
git commit -m "feat: 새 기능 추가"
```

### 2. 코드 리뷰

- Pull Request 생성
- CI/CD 파이프라인 통과 확인
- 리뷰 반영 및 머지

### 3. 디버깅 팁

- `console.log()` 대신 VSCode 디버거 사용
- Watch 창에서 변수 모니터링
- Call Stack으로 함수 호출 추적
- Breakpoint 조건 설정 가능

## 참고 자료

- [Spring Boot Tools for VSCode](https://marketplace.visualstudio.com/items?itemName=vmware.vscode-spring-boot)
- [Java in VSCode](https://code.visualstudio.com/docs/java/java-tutorial)
- [Debugging in VSCode](https://code.visualstudio.com/docs/editor/debugging)
- [Tasks in VSCode](https://code.visualstudio.com/docs/editor/tasks)
