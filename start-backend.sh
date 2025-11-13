#!/bin/bash

echo "=========================================="
echo "의약품 허가심사 검토 시스템 - Backend 시작"
echo "=========================================="
echo ""

# 환경 확인
echo "[1/4] 환경 확인 중..."

# Java 버전 확인
if ! command -v java &> /dev/null; then
    echo "❌ Java가 설치되어 있지 않습니다. Java 17 이상을 설치해주세요."
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 17 ]; then
    echo "❌ Java 17 이상이 필요합니다. 현재 버전: $JAVA_VERSION"
    exit 1
fi

echo "✅ Java 버전: $(java -version 2>&1 | head -n 1)"

# Docker 컨테이너 확인
echo ""
echo "[2/4] 인프라 서비스 확인 중..."

services=("drug-approval-mysql" "drug-approval-chroma" "drug-approval-opensearch" "drug-approval-ollama")
all_running=true

for service in "${services[@]}"; do
    if ! docker ps --format '{{.Names}}' | grep -q "^${service}$"; then
        echo "⚠️  $service 가 실행되지 않았습니다."
        all_running=false
    else
        echo "✅ $service 실행 중"
    fi
done

if [ "$all_running" = false ]; then
    echo ""
    echo "인프라 서비스를 시작하시겠습니까? (y/n)"
    read -r response
    if [ "$response" = "y" ]; then
        echo "인프라 서비스 시작 중..."
        docker-compose up -d mysql chromadb opensearch ollama
        echo "서비스 초기화 대기 중 (30초)..."
        sleep 30
    else
        echo "인프라 서비스를 먼저 시작해주세요: docker-compose up -d mysql chromadb opensearch ollama"
        exit 1
    fi
fi

# Backend 디렉토리로 이동
cd backend || exit 1

# Maven 빌드
echo ""
echo "[3/4] Maven 빌드 중..."
./mvnw clean compile

if [ $? -ne 0 ]; then
    echo "❌ Maven 빌드 실패"
    exit 1
fi

echo "✅ 빌드 완료"

# Spring Boot 실행
echo ""
echo "[4/4] Spring Boot 애플리케이션 시작 중..."
echo ""
echo "=========================================="
echo "Backend 실행 중: http://localhost:8080"
echo "=========================================="
echo ""

./mvnw spring-boot:run
