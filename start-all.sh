#!/bin/bash

echo "=========================================="
echo "의약품 허가심사 검토 시스템 - 전체 시작"
echo "=========================================="
echo ""

# 1. Docker 인프라 시작
echo "[1/4] Docker 인프라 서비스 시작 중..."
docker-compose up -d mysql chromadb opensearch ollama

echo "✅ Docker 서비스 시작 완료"
echo ""

# 2. 서비스 초기화 대기
echo "[2/4] 서비스 초기화 대기 중 (30초)..."
sleep 30
echo "✅ 초기화 완료"
echo ""

# 3. Ollama 모델 확인
echo "[3/4] Ollama 모델 확인 중..."
if ! docker exec drug-approval-ollama ollama list | grep -q "llama3.2"; then
    echo "Ollama 모델을 다운로드합니다 (시간이 소요될 수 있습니다)..."
    docker exec drug-approval-ollama ollama pull llama3.2
    echo "✅ 모델 다운로드 완료"
else
    echo "✅ Ollama 모델이 이미 설치되어 있습니다."
fi
echo ""

# 4. 애플리케이션 실행 안내
echo "[4/4] 애플리케이션 실행"
echo ""
echo "=========================================="
echo "인프라 준비 완료!"
echo "=========================================="
echo ""
echo "이제 다음 명령어로 애플리케이션을 실행하세요:"
echo ""
echo "Backend 실행:"
echo "  ./start-backend.sh"
echo "  또는"
echo "  cd backend && ./mvnw spring-boot:run"
echo ""
echo "Frontend 실행:"
echo "  ./start-frontend.sh"
echo "  또는"
echo "  cd frontend && npm run dev"
echo ""
echo "또는 VSCode에서:"
echo "  F5 키를 눌러 'Full Stack' 실행 구성 선택"
echo ""
echo "=========================================="
echo "서비스 URL:"
echo "  - Backend API: http://localhost:8080/api"
echo "  - Frontend: http://localhost:3000"
echo "  - MySQL: localhost:3306"
echo "  - ChromaDB: http://localhost:8000"
echo "  - OpenSearch: http://localhost:9200"
echo "  - Ollama: http://localhost:11434"
echo "=========================================="
